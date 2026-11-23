from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any

class HealthResponse(BaseModel):
    status: str
    service: str
    version: str
    engine: str
    tesseract_available: bool

class BoundingBox(BaseModel):
    x: int
    y: int
    width: int
    height: int

class OCRLine(BaseModel):
    text: str
    confidence: float
    bbox: Optional[BoundingBox] = None

class OCRExtractResponse(BaseModel):
    raw_text: str
    language: str
    lines: List[OCRLine]
    avg_confidence: float
    engine: str
    processing_time_ms: float
    is_synthetic: bool = False

class DetectedRegion(BaseModel):
    region_type: str  # e.g., "MRP_PANEL", "MANUFACTURER_BLOCK", "SPECIFICATION_TABLE", "CONSUMER_CARE"
    bbox: BoundingBox
    confidence: float
    sample_text: Optional[str] = None

class RegionDetectResponse(BaseModel):
    regions: List[DetectedRegion]
    engine: str
    processing_time_ms: float

class ExtractedFieldItem(BaseModel):
    field_type: str  # "MRP", "NET_QTY", "MANUFACTURER", "CONSUMER_CARE", "COUNTRY_OF_ORIGIN", "MFG_DATE"
    raw_value: str
    normalized_value: Optional[str] = None
    unit: Optional[str] = None
    confidence: float
    bbox: Optional[BoundingBox] = None
    source: str = "IMAGE"

class NLPExtractRequest(BaseModel):
    text: str
    context: Optional[Dict[str, Any]] = None

class NLPExtractResponse(BaseModel):
    fields: List[ExtractedFieldItem]
    engine: str
    processing_time_ms: float
