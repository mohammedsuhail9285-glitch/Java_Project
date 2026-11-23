from fastapi import APIRouter, UploadFile, File, Form, HTTPException
from typing import Optional
from PIL import Image
import io

from app.models.schemas import RegionDetectResponse
from app.vision.region_detector import RegionDetector

router = APIRouter(prefix="/vision", tags=["Vision"])

@router.post("/detect-regions", response_model=RegionDetectResponse)
async def detect_regions(
    file: Optional[UploadFile] = File(None),
    width: Optional[int] = Form(800),
    height: Optional[int] = Form(600)
):
    try:
        w, h = width or 800, height or 600
        if file is not None:
            image_bytes = await file.read()
            with Image.open(io.BytesIO(image_bytes)) as img:
                w, h = img.size

        return RegionDetector.detect_regions((w, h))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Region detection failed: {str(e)}")
