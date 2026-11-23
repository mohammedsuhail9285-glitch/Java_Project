import time
from typing import List, Optional
from app.models.schemas import ExtractedFieldItem, BoundingBox, NLPExtractResponse
from app.nlp import regex_patterns as rp

class FieldExtractor:
    """
    Extracts statutory fields (MRP, Net Quantity, Manufacturer, etc.)
    from OCR text buffers using pattern matching.
    """

    @staticmethod
    def extract_fields(text: str, ocr_lines: list = None) -> NLPExtractResponse:
        start_time = time.time()
        fields: List[ExtractedFieldItem] = []

        # 1. Retail Sale Price (MRP)
        mrp_match = rp.MRP_PATTERN.search(text)
        if mrp_match:
            val = mrp_match.group(1).strip()
            bbox = FieldExtractor._find_line_bbox(ocr_lines, val) or BoundingBox(x=35, y=140, width=280, height=28)
            fields.append(ExtractedFieldItem(
                field_type="MRP",
                raw_value=val,
                normalized_value=val,
                unit="INR",
                confidence=95.0,
                bbox=bbox,
                source="IMAGE"
            ))

        # 2. Net Quantity & Unit
        qty_match = rp.NET_QTY_PATTERN.search(text)
        if qty_match:
            val = qty_match.group(1).strip()
            unit = qty_match.group(2).strip()
            bbox = FieldExtractor._find_line_bbox(ocr_lines, val) or BoundingBox(x=35, y=140, width=200, height=28)
            fields.append(ExtractedFieldItem(
                field_type="NET_QTY",
                raw_value=f"{val} {unit}",
                normalized_value=val,
                unit=unit,
                confidence=94.0,
                bbox=bbox,
                source="IMAGE"
            ))

        # 3. Manufacturer / Packer
        mfg_match = rp.MANUFACTURER_PATTERN.search(text)
        if mfg_match:
            raw = mfg_match.group(1).strip()
            # Clean up trailing unwanted tokens
            clean = raw.split("|")[0].strip()
            bbox = FieldExtractor._find_line_bbox(ocr_lines, clean) or BoundingBox(x=35, y=190, width=540, height=25)
            fields.append(ExtractedFieldItem(
                field_type="MANUFACTURER",
                raw_value=clean,
                normalized_value=clean,
                unit=None,
                confidence=91.0,
                bbox=bbox,
                source="IMAGE"
            ))

        # 4. Importer (if imported)
        imp_match = rp.IMPORTER_PATTERN.search(text)
        if imp_match:
            clean = imp_match.group(1).strip().split("|")[0].strip()
            bbox = FieldExtractor._find_line_bbox(ocr_lines, clean) or BoundingBox(x=35, y=190, width=550, height=25)
            fields.append(ExtractedFieldItem(
                field_type="IMPORTER",
                raw_value=clean,
                normalized_value=clean,
                unit=None,
                confidence=90.0,
                bbox=bbox,
                source="IMAGE"
            ))

        # 5. Consumer Care (Phone and/or Email)
        phone_match = rp.CONSUMER_CARE_PHONE_PATTERN.search(text)
        email_match = rp.EMAIL_PATTERN.search(text)
        care_tokens = []
        if phone_match:
            care_tokens.append(f"Phone: {phone_match.group(2).strip()}")
        if email_match:
            care_tokens.append(f"Email: {email_match.group(1).strip()}")

        if care_tokens:
            combined = " | ".join(care_tokens)
            bbox = FieldExtractor._find_line_bbox(ocr_lines, care_tokens[0]) or BoundingBox(x=35, y=230, width=520, height=25)
            fields.append(ExtractedFieldItem(
                field_type="CONSUMER_CARE",
                raw_value=combined,
                normalized_value=combined,
                unit=None,
                confidence=92.0,
                bbox=bbox,
                source="IMAGE"
            ))

        # 6. Country of Origin
        origin_match = rp.ORIGIN_PATTERN.search(text)
        if origin_match:
            origin = origin_match.group(1).strip().split("|")[0].strip()
            bbox = FieldExtractor._find_line_bbox(ocr_lines, origin) or BoundingBox(x=35, y=230, width=250, height=25)
            fields.append(ExtractedFieldItem(
                field_type="COUNTRY_OF_ORIGIN",
                raw_value=origin,
                normalized_value=origin,
                unit=None,
                confidence=93.0,
                bbox=bbox,
                source="IMAGE"
            ))

        # 7. Date of Manufacture / Packing
        date_match = rp.DATE_PATTERN.search(text)
        if date_match:
            date_val = date_match.group(1).strip()
            bbox = FieldExtractor._find_line_bbox(ocr_lines, date_val) or BoundingBox(x=35, y=270, width=200, height=24)
            fields.append(ExtractedFieldItem(
                field_type="MFG_DATE",
                raw_value=date_val,
                normalized_value=date_val,
                unit=None,
                confidence=91.5,
                bbox=bbox,
                source="IMAGE"
            ))

        proc_time = round((time.time() - start_time) * 1000, 2)
        return NLPExtractResponse(
            fields=fields,
            engine="regex_nlp_extractor",
            processing_time_ms=proc_time
        )

    @staticmethod
    def _find_line_bbox(ocr_lines: list, keyword: str) -> Optional[BoundingBox]:
        if not ocr_lines or not keyword:
            return None
        kw_lower = keyword.lower()
        for line in ocr_lines:
            text = getattr(line, 'text', '') if hasattr(line, 'text') else str(line.get('text', ''))
            if kw_lower in text.lower():
                return getattr(line, 'bbox', None) if hasattr(line, 'bbox') else line.get('bbox')
        return None
