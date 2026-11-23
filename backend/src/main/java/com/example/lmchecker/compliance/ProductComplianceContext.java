package com.example.lmchecker.compliance;

import com.example.lmchecker.entity.*;

import java.util.*;

public class ProductComplianceContext {
    private final Product product;
    private final ProductListing listing;
    private final ProductImage image;
    private final OCRResult ocrResult;
    private final List<ExtractedField> extractedFields;
    private final Map<String, ExtractedField> fieldMap = new HashMap<>();
    private final Map<String, ExtractedField> listingFieldMap = new HashMap<>();

    public ProductComplianceContext(Product product, ProductListing listing, ProductImage image,
                                  OCRResult ocrResult, List<ExtractedField> extractedFields) {
        this.product = product;
        this.listing = listing;
        this.image = image;
        this.ocrResult = ocrResult;
        this.extractedFields = extractedFields != null ? extractedFields : Collections.emptyList();

        for (ExtractedField f : this.extractedFields) {
            if (f.getFieldType() != null) {
                String key = f.getFieldType().name().toUpperCase(Locale.ROOT);
                if (f.getSource() == FieldSource.LISTING) {
                    listingFieldMap.put(key, f);
                } else {
                    fieldMap.put(key, f);
                }
            }
        }
    }

    public Product getProduct() { return product; }
    public ProductListing getListing() { return listing; }
    public ProductImage getImage() { return image; }
    public OCRResult getOcrResult() { return ocrResult; }
    public List<ExtractedField> getExtractedFields() { return extractedFields; }

    public ExtractedField getExtractedField(String fieldType) {
        if (fieldType == null) return null;
        ExtractedField imgField = fieldMap.get(fieldType.toUpperCase(Locale.ROOT));
        if (imgField != null) return imgField;
        return listingFieldMap.get(fieldType.toUpperCase(Locale.ROOT));
    }

    public ExtractedField getImageExtractedField(String fieldType) {
        if (fieldType == null) return null;
        return fieldMap.get(fieldType.toUpperCase(Locale.ROOT));
    }

    public ExtractedField getListingExtractedField(String fieldType) {
        if (fieldType == null) return null;
        return listingFieldMap.get(fieldType.toUpperCase(Locale.ROOT));
    }

    public boolean hasPackageImage() {
        return image != null || ocrResult != null || !fieldMap.isEmpty();
    }

    public boolean hasExtractedField(String fieldType) {
        return getExtractedField(fieldType) != null;
    }

    public String getRawOCRText() {
        return ocrResult != null ? ocrResult.getRawText() : "";
    }
}
