from typing import List
from app.models.schemas import OCRLine, BoundingBox, OCRExtractResponse
import time

class FallbackOCREngine:
    """
    Offline fallback OCR engine for local development and testing
    when Tesseract OCR is not installed on the host system.
    """

    @staticmethod
    def extract_text(filename: str, image_size: tuple[int, int]) -> OCRExtractResponse:
        start_time = time.time()
        width, height = image_size
        filename_lower = filename.lower()

        lines: List[OCRLine] = []

        if "missing_mrp" in filename_lower or "no_mrp" in filename_lower:
            lines = [
                OCRLine(text="HERBAL CLEAN HOME CARE COMMODITY", confidence=93.0, bbox=BoundingBox(x=35, y=90, width=520, height=30)),
                OCRLine(text="Net Qty: 1 L | Standard Concentrated Formula", confidence=94.0, bbox=BoundingBox(x=35, y=140, width=480, height=28)),
                OCRLine(text="Mfg By: EcoSpark Home Solutions, Vadodara - 390001", confidence=91.0, bbox=BoundingBox(x=35, y=190, width=540, height=25)),
                OCRLine(text="Country of Origin: India | Care: 1800-222-3344", confidence=92.0, bbox=BoundingBox(x=35, y=230, width=500, height=25)),
                OCRLine(text="Mfg Date: 06/2026 | Batch: EC-2026", confidence=90.0, bbox=BoundingBox(x=35, y=270, width=400, height=24))
            ]
        elif "missing_qty" in filename_lower or "no_qty" in filename_lower:
            lines = [
                OCRLine(text="VEDIKA HERBAL WELLNESS EXTRACT", confidence=94.0, bbox=BoundingBox(x=35, y=90, width=510, height=30)),
                OCRLine(text="MRP Rs. 299.00 (Inclusive of all taxes)", confidence=95.0, bbox=BoundingBox(x=35, y=140, width=490, height=28)),
                OCRLine(text="Mfg By: Vedika Ayurveda & Naturals, Haridwar", confidence=92.0, bbox=BoundingBox(x=35, y=190, width=530, height=25)),
                OCRLine(text="Country of Origin: India | Care: 1800-200-1122", confidence=93.0, bbox=BoundingBox(x=35, y=230, width=510, height=25)),
                OCRLine(text="Mfg Date: 05/2026", confidence=91.0, bbox=BoundingBox(x=35, y=270, width=350, height=24))
            ]
        elif "invalid_unit" in filename_lower:
            lines = [
                OCRLine(text="DELUXE PACKAGED SPICES ASSORTMENT", confidence=92.0, bbox=BoundingBox(x=35, y=90, width=530, height=30)),
                OCRLine(text="Net Qty: 500 pouches | MRP Rs. 350.00 (Incl. taxes)", confidence=93.0, bbox=BoundingBox(x=35, y=140, width=520, height=28)),
                OCRLine(text="Mfg By: Golden Harvest Foods Ltd, Guntur", confidence=91.0, bbox=BoundingBox(x=35, y=190, width=500, height=25)),
                OCRLine(text="Country of Origin: India | Helpline: 1800-999-0000", confidence=92.0, bbox=BoundingBox(x=35, y=230, width=520, height=25)),
                OCRLine(text="Mfg Date: 07/2026", confidence=90.0, bbox=BoundingBox(x=35, y=270, width=320, height=24))
            ]
        elif "overcharge" in filename_lower or "price_mismatch" in filename_lower:
            lines = [
                OCRLine(text="HIMALAYAN ORGANIC NATURAL HONEY", confidence=95.0, bbox=BoundingBox(x=35, y=90, width=510, height=30)),
                OCRLine(text="Net Qty: 500 g | MRP Rs. 299.00 (Incl. of all taxes)", confidence=95.0, bbox=BoundingBox(x=35, y=140, width=540, height=28)),
                OCRLine(text="Mfg By: Himalayan Apiaries Ltd, Shimla - 171001", confidence=92.0, bbox=BoundingBox(x=35, y=190, width=530, height=25)),
                OCRLine(text="Country of Origin: India | Care: support@himalayannectar.in", confidence=93.0, bbox=BoundingBox(x=35, y=230, width=550, height=25)),
                OCRLine(text="Batch: HM-90 | Mfg Date: 08/2026", confidence=91.0, bbox=BoundingBox(x=35, y=270, width=420, height=24))
            ]
        elif "compliant" in filename_lower:
            lines = [
                OCRLine(text="STANDARDIZED PACKAGED COMMODITY", confidence=96.0, bbox=BoundingBox(x=35, y=90, width=530, height=30)),
                OCRLine(text="Net Qty: 500 g | MRP Rs. 150.00 (Incl. of all taxes)", confidence=95.5, bbox=BoundingBox(x=35, y=140, width=520, height=28)),
                OCRLine(text="Mfg By: Standard Consumer Goods Ltd, Pune - 411001", confidence=93.0, bbox=BoundingBox(x=35, y=190, width=550, height=25)),
                OCRLine(text="Country of Origin: India | Consumer Care: care@standardgoods.in", confidence=94.0, bbox=BoundingBox(x=35, y=230, width=570, height=25)),
                OCRLine(text="Batch: SC-2026 | Mfg Date: 08/2026", confidence=92.0, bbox=BoundingBox(x=35, y=270, width=450, height=24))
            ]
        elif "atta" in filename_lower:
            lines = [
                OCRLine(text="SWARNA SHAKTI WHOLE WHEAT ATTA", confidence=96.5, bbox=BoundingBox(x=35, y=90, width=550, height=30)),
                OCRLine(text="Net Qty: 5 kg | MRP Rs. 320.00 (Incl. of all taxes)", confidence=95.0, bbox=BoundingBox(x=35, y=140, width=580, height=28)),
                OCRLine(text="Mfg By: Swarna Shakti Consumer Ltd, Pune - 411001", confidence=92.0, bbox=BoundingBox(x=35, y=190, width=540, height=25)),
                OCRLine(text="Country of Origin: India | Care: 1800-200-1122", confidence=94.0, bbox=BoundingBox(x=35, y=230, width=520, height=25)),
                OCRLine(text="Batch: AT-2026-08 | Mfg Date: 08/2026 | Best Before 6 Months", confidence=91.5, bbox=BoundingBox(x=35, y=270, width=560, height=24))
            ]
        elif "facewash" in filename_lower:
            lines = [
                OCRLine(text="VEDIKA PURIFYING NEEM FACE WASH", confidence=94.0, bbox=BoundingBox(x=35, y=90, width=540, height=30)),
                OCRLine(text="Net Vol: 150 ml. | MRP Rs. 185.00 (Incl. Taxes)", confidence=93.5, bbox=BoundingBox(x=35, y=140, width=520, height=28)),
                OCRLine(text="Mfg By: Vedika Ayurveda & Naturals, Haridwar - 249401", confidence=91.0, bbox=BoundingBox(x=35, y=190, width=550, height=25)),
                OCRLine(text="Customer Care: care@vedikaherbal.in (No phone found)", confidence=86.0, bbox=BoundingBox(x=35, y=230, width=510, height=25)),
                OCRLine(text="Country of Origin: India | Mfg: 05/2026", confidence=92.0, bbox=BoundingBox(x=35, y=270, width=480, height=24))
            ]
        elif "sheetmask" in filename_lower:
            lines = [
                OCRLine(text="APEX GLOW HYDRATING SHEET MASK", confidence=95.0, bbox=BoundingBox(x=35, y=90, width=530, height=30)),
                OCRLine(text="Net Qty: 25 g | MRP Rs. 499.00 (Incl. of all taxes)", confidence=94.5, bbox=BoundingBox(x=35, y=140, width=510, height=28)),
                OCRLine(text="Mfg: Apex Global Formulations, Guangzhou", confidence=90.0, bbox=BoundingBox(x=35, y=190, width=480, height=25)),
                OCRLine(text="Batch: AG-2026-91 | NO IMPORTER ADDRESS FOUND", confidence=88.0, bbox=BoundingBox(x=35, y=230, width=520, height=25)),
                OCRLine(text="Origin: Made in China", confidence=89.0, bbox=BoundingBox(x=35, y=270, width=320, height=24))
            ]
        elif "protein" in filename_lower:
            lines = [
                OCRLine(text="NUTRIBOOST PLANT PROTEIN POWDER", confidence=96.0, bbox=BoundingBox(x=35, y=90, width=560, height=30)),
                OCRLine(text="Net Wt: 1 kg | MRP Rs. 2199.00 (Incl. of all taxes)", confidence=95.5, bbox=BoundingBox(x=35, y=140, width=540, height=28)),
                OCRLine(text="Mfg By: NutriBoost Labs, Andheri, Mumbai - 400053", confidence=93.0, bbox=BoundingBox(x=35, y=190, width=550, height=25)),
                OCRLine(text="Customer Care: support@nutriboost.in | Country of Origin: India", confidence=92.5, bbox=BoundingBox(x=35, y=230, width=570, height=25)),
                OCRLine(text="Mfg Date: 07/2026 | Exp: 07/2027", confidence=94.0, bbox=BoundingBox(x=35, y=270, width=420, height=24))
            ]
        elif "charger" in filename_lower:
            lines = [
                OCRLine(text="VOLTMAX 65W GaN FAST CHARGER", confidence=95.0, bbox=BoundingBox(x=35, y=90, width=520, height=30)),
                OCRLine(text="Net Qty: 1 Unit | MRP Rs. 1899.00 (Incl. of all taxes)", confidence=94.0, bbox=BoundingBox(x=35, y=140, width=540, height=28)),
                OCRLine(text="Imported & Packed by: Apex Global Traders, Okhla, New Delhi", confidence=91.0, bbox=BoundingBox(x=35, y=190, width=570, height=25)),
                OCRLine(text="Country of Origin: China | Care: support@apexglobalkart.com", confidence=92.0, bbox=BoundingBox(x=35, y=230, width=560, height=25))
            ]
        else:
            # Generic legal metrology package parsing
            lines = [
                OCRLine(text="STANDARDIZED PACKAGED COMMODITY LABEL", confidence=92.0, bbox=BoundingBox(x=35, y=90, width=450, height=30)),
                OCRLine(text="Net Qty: 500 g | MRP Rs. 150.00 (Incl. of all taxes)", confidence=94.0, bbox=BoundingBox(x=35, y=140, width=490, height=28)),
                OCRLine(text="Manufactured & Packed by: Standard Consumer Goods Ltd", confidence=89.0, bbox=BoundingBox(x=35, y=190, width=520, height=25)),
                OCRLine(text="Country of Origin: India | Consumer Care: care@consumerbrand.in", confidence=91.0, bbox=BoundingBox(x=35, y=230, width=540, height=25))
            ]

        raw_text = "\n".join([line.text for line in lines])
        avg_confidence = round(sum(l.confidence for l in lines) / len(lines), 2)
        proc_time = round((time.time() - start_time) * 1000, 2)

        return OCRExtractResponse(
            raw_text=raw_text,
            language="eng",
            lines=lines,
            avg_confidence=avg_confidence,
            engine="DEMO_FALLBACK",
            processing_time_ms=proc_time,
            is_synthetic=True
        )
