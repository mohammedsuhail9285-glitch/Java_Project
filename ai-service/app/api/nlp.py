from fastapi import APIRouter, HTTPException, Body
from app.models.schemas import NLPExtractRequest, NLPExtractResponse
from app.nlp.field_extractor import FieldExtractor

router = APIRouter(prefix="/nlp", tags=["NLP"])

@router.post("/extract-fields", response_model=NLPExtractResponse)
async def extract_fields(request: NLPExtractRequest = Body(...)):
    try:
        ocr_lines = request.context.get("ocr_lines", []) if request.context else []
        return FieldExtractor.extract_fields(request.text, ocr_lines=ocr_lines)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Field extraction failed: {str(e)}")
