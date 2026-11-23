from fastapi import APIRouter, UploadFile, File, Form, HTTPException
from typing import Optional
from PIL import Image
import io

from app.models.schemas import OCRExtractResponse
from app.ocr.preprocessor import ImagePreprocessor
from app.ocr.tesseract_engine import TesseractOCREngine

router = APIRouter(prefix="/ocr", tags=["OCR"])

@router.post("/extract", response_model=OCRExtractResponse)
async def extract_ocr(
    file: Optional[UploadFile] = File(None),
    filename: Optional[str] = Form("upload.png"),
    language: Optional[str] = Form("eng")
):
    try:
        if file is not None:
            image_bytes = await file.read()
            fname = file.filename or filename or "upload.png"
        else:
            raise HTTPException(status_code=400, detail="Image file must be provided")

        processed_img, _ = ImagePreprocessor.preprocess_image(image_bytes)
        result = TesseractOCREngine.extract_text(processed_img, filename=fname, lang=language)
        return result
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"OCR extraction failed: {str(e)}")
