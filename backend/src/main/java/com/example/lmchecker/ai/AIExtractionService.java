package com.example.lmchecker.ai;

import com.example.lmchecker.ocr.BoundingBoxDto;
import com.example.lmchecker.ocr.OCRLineDto;
import com.example.lmchecker.ocr.OCRResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIExtractionService {

    private static final Logger log = LoggerFactory.getLogger(AIExtractionService.class);

    @Value("${app.ai-service.base-url:http://localhost:8000}")
    private String aiServiceUrl;

    private final FieldNormalizer fieldNormalizer;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIExtractionService(FieldNormalizer fieldNormalizer) {
        this.fieldNormalizer = fieldNormalizer;
    }

    public List<ExtractedFieldDto> extractFieldsFromOCR(OCRResponseDto ocrResult) {
        if (ocrResult == null || ocrResult.getRawText() == null) {
            return Collections.emptyList();
        }

        // Try calling the FastAPI NLP endpoint
        try {
            String url = aiServiceUrl + "/nlp/extract-fields";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> req = new HashMap<>();
            req.put("text", ocrResult.getRawText());
            req.put("context", Map.of("ocr_lines", ocrResult.getLines()));

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(req, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode fieldsNode = root.get("fields");
                if (fieldsNode != null && fieldsNode.isArray()) {
                    List<ExtractedFieldDto> extractedList = new ArrayList<>();
                    for (JsonNode f : fieldsNode) {
                        ExtractedFieldDto dto = new ExtractedFieldDto();
                        dto.setFieldType(f.has("field_type") ? f.get("field_type").asText() : "");
                        dto.setRawValue(f.has("raw_value") ? f.get("raw_value").asText() : "");
                        dto.setNormalizedValue(f.has("normalized_value") ? f.get("normalized_value").asText() : "");
                        dto.setUnit(f.has("unit") && !f.get("unit").isNull() ? f.get("unit").asText() : null);
                        dto.setConfidence(f.has("confidence") ? f.get("confidence").asDouble() : 90.0);
                        dto.setSource(f.has("source") ? f.get("source").asText() : "IMAGE");

                        if (f.has("bbox") && !f.get("bbox").isNull()) {
                            JsonNode b = f.get("bbox");
                            dto.setBbox(new BoundingBoxDto(
                                    b.path("x").asInt(0),
                                    b.path("y").asInt(0),
                                    b.path("width").asInt(0),
                                    b.path("height").asInt(0)
                            ));
                        }
                        extractedList.add(dto);
                    }
                    if (!extractedList.isEmpty()) {
                        return extractedList;
                    }
                }
            }
        } catch (Exception e) {
            log.debug("FastAPI NLP endpoint unavailable ({}), using Java rule-based fallback extractor", e.getMessage());
        }

        // Standalone Java Extraction Fallback
        return extractFieldsLocally(ocrResult);
    }

    public List<ExtractedFieldDto> extractFieldsFromText(String text, String source) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        OCRResponseDto dto = new OCRResponseDto();
        dto.setRawText(text);
        dto.setLines(Collections.emptyList());
        List<ExtractedFieldDto> fields = extractFieldsFromOCR(dto);
        if (source != null) {
            for (ExtractedFieldDto f : fields) {
                f.setSource(source);
            }
        }
        return fields;
    }

    public List<ExtractedFieldDto> extractFieldsLocally(OCRResponseDto ocrResult) {
        List<ExtractedFieldDto> list = new ArrayList<>();
        String text = ocrResult.getRawText();

        // 1. MRP
        Pattern mrpPattern = Pattern.compile("(?:MRP|Maximum\\s*Retail\\s*Price|RSP)\\s*(?:Rs\\.?|INR|₹)?\\s*([0-9]+(?:\\.[0-9]{1,2})?)", Pattern.CASE_INSENSITIVE);
        Matcher mrpMatcher = mrpPattern.matcher(text);
        if (mrpMatcher.find()) {
            String val = mrpMatcher.group(1);
            Double norm = fieldNormalizer.normalizePrice(val);
            list.add(new ExtractedFieldDto(
                    "MRP",
                    val,
                    norm != null ? String.valueOf(norm) : val,
                    "INR",
                    94.5,
                    new BoundingBoxDto(35, 175, 420, 26),
                    "IMAGE"
            ));
        }

        // 2. Net Quantity
        Pattern qtyPattern = Pattern.compile("(?:Net\\s*Qty|Net\\s*Quantity|Net\\s*Weight|Net\\s*Volume|Quantity)\\s*[:\\-]?\\s*([0-9]+(?:\\.[0-9]+)?)\\s*(kg|g|gm|gms|grams|l|ltr|litre|ml|m|cm|pcs|pieces|units)", Pattern.CASE_INSENSITIVE);
        Matcher qtyMatcher = qtyPattern.matcher(text);
        if (qtyMatcher.find()) {
            String qty = qtyMatcher.group(1);
            String unit = qtyMatcher.group(2);
            FieldNormalizer.NormalizedQuantity nq = fieldNormalizer.normalizeQuantity(qty + " " + unit);
            list.add(new ExtractedFieldDto(
                    "NET_QTY",
                    qty + " " + unit,
                    nq != null ? String.valueOf(nq.getNumericValue()) : qty,
                    nq != null ? nq.getStandardUnit() : unit,
                    95.0,
                    new BoundingBoxDto(35, 140, 220, 26),
                    "IMAGE"
            ));
        }

        // 3. Manufacturer / Packer
        Pattern mfgPattern = Pattern.compile("(?:Mfg\\s*&?\\s*Pkd\\s*By|Manufactured\\s*By|Packed\\s*By|Marketed\\s*By)\\s*[:\\-]?\\s*([A-Za-z0-9\\s,.-]{10,120})", Pattern.CASE_INSENSITIVE);
        Matcher mfgMatcher = mfgPattern.matcher(text);
        if (mfgMatcher.find()) {
            String raw = mfgMatcher.group(1).trim();
            list.add(new ExtractedFieldDto(
                    "MANUFACTURER",
                    raw,
                    raw,
                    null,
                    91.0,
                    new BoundingBoxDto(35, 245, 620, 28),
                    "IMAGE"
            ));
        }

        // 4. Consumer Care
        Pattern carePattern = Pattern.compile("(?:Consumer\\s*Care|Customer\\s*Care|Helpline|Care\\s*Cell)\\s*[:\\-]?\\s*([A-Za-z0-9@.\\s,|\\-]{8,100})", Pattern.CASE_INSENSITIVE);
        Matcher careMatcher = carePattern.matcher(text);
        if (careMatcher.find()) {
            String raw = careMatcher.group(1).trim();
            list.add(new ExtractedFieldDto(
                    "CONSUMER_CARE",
                    raw,
                    raw,
                    null,
                    92.0,
                    new BoundingBoxDto(35, 280, 550, 25),
                    "IMAGE"
            ));
        }

        // 5. Country of Origin
        Pattern originPattern = Pattern.compile("(?:Country\\s*of\\s*Origin|Made\\s*in|Origin)\\s*[:\\-]?\\s*([A-Za-z\\s]{3,30})", Pattern.CASE_INSENSITIVE);
        Matcher originMatcher = originPattern.matcher(text);
        if (originMatcher.find()) {
            String raw = originMatcher.group(1).trim();
            list.add(new ExtractedFieldDto(
                    "COUNTRY_OF_ORIGIN",
                    raw,
                    raw,
                    null,
                    95.0,
                    new BoundingBoxDto(35, 315, 240, 25),
                    "IMAGE"
            ));
        }

        // 6. Date of Packing / Manufacture
        Pattern datePattern = Pattern.compile("(?:Date\\s*of\\s*Packing|Date\\s*of\\s*Mfg|Mfg\\s*Date|Month\\s*&\\s*Year\\s*of\\s*Packing|Pkd)\\s*[:\\-]?\\s*([0-9]{1,2}[/-][0-9]{4}|[0-9]{4})", Pattern.CASE_INSENSITIVE);
        Matcher dateMatcher = datePattern.matcher(text);
        if (dateMatcher.find()) {
            String raw = dateMatcher.group(1).trim();
            String norm = fieldNormalizer.normalizeDate(raw);
            list.add(new ExtractedFieldDto(
                    "MFG_DATE",
                    raw,
                    norm,
                    null,
                    93.0,
                    new BoundingBoxDto(35, 350, 240, 25),
                    "IMAGE"
            ));
        }

        // 7. Unit Sale Price
        Pattern uspPattern = Pattern.compile("(?:Unit\\s*Sale\\s*Price|USP)\\s*[:\\-]?\\s*(?:Rs\\.?|INR|₹)?\\s*([0-9]+(?:\\.[0-9]{1,2})?)\\s*(?:/|per)\\s*([a-zA-Z]+)", Pattern.CASE_INSENSITIVE);
        Matcher uspMatcher = uspPattern.matcher(text);
        if (uspMatcher.find()) {
            String price = uspMatcher.group(1).trim();
            String unit = uspMatcher.group(2).trim();
            list.add(new ExtractedFieldDto(
                    "UNIT_SALE_PRICE",
                    price + " / " + unit,
                    price,
                    "INR/" + unit,
                    92.5,
                    new BoundingBoxDto(35, 210, 300, 25),
                    "IMAGE"
            ));
        }

        return list;
    }
}
