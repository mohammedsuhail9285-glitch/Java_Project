from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import time
import shutil
from app.models.schemas import HealthResponse

app = FastAPI(
    title="RegTech Sentinel AI/OCR Service",
    description="Offline OCR, Computer Vision & NLP Entity Extraction for Legal Metrology Declarations",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

from app.api.ocr import router as ocr_router
from app.api.vision import router as vision_router
from app.api.nlp import router as nlp_router
from app.api.freight import router as freight_router

app.include_router(ocr_router)
app.include_router(vision_router)
app.include_router(nlp_router)
app.include_router(freight_router)


import logging
from app.ocr.tesseract_engine import TesseractOCREngine

logger = logging.getLogger("ai_service")
logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")

@app.on_event("startup")
def startup_event():
    TesseractOCREngine.configure()
    has_tess = TesseractOCREngine.is_available()
    cmd = TesseractOCREngine.get_tesseract_cmd()
    engine_name = "TESSERACT" if has_tess else "SYNTHETIC"
    log_msg = f"[OCR ENGINE] Active Engine: {engine_name} (path: {cmd})"
    logger.info(log_msg)
    print(log_msg, flush=True)

def is_tesseract_installed() -> bool:
    return TesseractOCREngine.is_available()

@app.get("/health", response_model=HealthResponse)
def health_check():
    has_tess = is_tesseract_installed()
    return HealthResponse(
        status="UP",
        service="RegTech Sentinel AI/OCR Microservice",
        version="1.0.0",
        engine="TESSERACT" if has_tess else "SYNTHETIC",
        tesseract_available=has_tess
    )

@app.get("/")
def root():
    return {
        "service": "RegTech Sentinel AI/OCR Service",
        "status": "UP",
        "docs": "/docs",
        "health": "/health"
    }
