import os
import shutil
import time
import logging
from PIL import Image
from typing import List, Optional
import pytesseract
from pytesseract import Output
from app.models.schemas import OCRLine, BoundingBox, OCRExtractResponse
from app.ocr.fallback_engine import FallbackOCREngine
from app.ocr.preprocessor import ImagePreprocessor

logger = logging.getLogger("ai_service.ocr")

def resolve_tesseract_cmd() -> Optional[str]:
    # 1. Explicit environment variable
    env_path = os.environ.get("TESSERACT_PATH")
    if env_path and os.path.isfile(env_path):
        return env_path
    
    # 2. System PATH
    which_path = shutil.which("tesseract")
    if which_path:
        return which_path

    # 3. Standard Windows and Linux installation paths (portable and user-independent)
    local_app_data = os.environ.get("LOCALAPPDATA")
    program_files = os.environ.get("ProgramFiles", r"C:\Program Files")
    program_files_x86 = os.environ.get("ProgramFiles(x86)", r"C:\Program Files (x86)")
    candidate_paths = [
        os.path.expanduser(r"~\AppData\Local\Programs\Tesseract-OCR\tesseract.exe"),
        os.path.join(local_app_data, "Programs", "Tesseract-OCR", "tesseract.exe") if local_app_data else None,
        os.path.join(program_files, "Tesseract-OCR", "tesseract.exe"),
        os.path.join(program_files_x86, "Tesseract-OCR", "tesseract.exe"),
        "/usr/bin/tesseract",
        "/usr/local/bin/tesseract",
        "/opt/homebrew/bin/tesseract"
    ]
    for p in candidate_paths:
        if p and os.path.isfile(p):
            return p
    return None

class TesseractOCREngine:

    _configured = False
    _tesseract_cmd = None

    @classmethod
    def configure(cls):
        if not cls._configured:
            cmd = resolve_tesseract_cmd()
            if cmd:
                cls._tesseract_cmd = cmd
                pytesseract.pytesseract.tesseract_cmd = cmd
                tessdata_candidate = os.path.join(os.path.dirname(cmd), "tessdata")
                if os.path.isdir(tessdata_candidate) and "TESSDATA_PREFIX" not in os.environ:
                    os.environ["TESSDATA_PREFIX"] = tessdata_candidate
            cls._configured = True
        return cls._tesseract_cmd

    @classmethod
    def get_tesseract_cmd(cls) -> Optional[str]:
        cls.configure()
        return cls._tesseract_cmd

    @classmethod
    def is_available(cls) -> bool:
        cmd = cls.configure()
        return cmd is not None

    @classmethod
    def extract_text(cls, image, filename: str = "upload.png", lang: str = "eng") -> OCRExtractResponse:
        import io
        start_time = time.time()

        if isinstance(image, (bytes, bytearray)):
            try:
                image = Image.open(io.BytesIO(image))
            except Exception:
                return FallbackOCREngine.extract_text(filename, (800, 600))

        img_size = getattr(image, "size", (800, 600))
        if not cls.is_available():
            return FallbackOCREngine.extract_text(filename, img_size)

        try:
            # Preprocess with deskewing if image is tilted
            image = ImagePreprocessor.deskew(image)

            # Check if requested language is available in tesseract
            available_langs = pytesseract.get_languages(config='')
            use_lang = lang if lang in available_langs else "eng"

            data = pytesseract.image_to_data(image, lang=use_lang, output_type=Output.DICT)

            lines_map = {}
            n_boxes = len(data['text'])
            for i in range(n_boxes):
                text = data['text'][i].strip()
                conf = float(data['conf'][i])
                if not text or conf < 0:
                    continue

                line_num = data['line_num'][i]
                block_num = data['block_num'][i]
                key = (block_num, line_num)

                x = data['left'][i]
                y = data['top'][i]
                w = data['width'][i]
                h = data['height'][i]

                if key not in lines_map:
                    lines_map[key] = {
                        'words': [text],
                        'confs': [conf],
                        'bbox': [x, y, x + w, y + h]
                    }
                else:
                    lines_map[key]['words'].append(text)
                    lines_map[key]['confs'].append(conf)
                    b = lines_map[key]['bbox']
                    lines_map[key]['bbox'] = [min(b[0], x), min(b[1], y), max(b[2], x + w), max(b[3], y + h)]

            ocr_lines: List[OCRLine] = []
            for item in lines_map.values():
                line_text = " ".join(item['words'])
                avg_conf = round(sum(item['confs']) / len(item['confs']), 2)
                bbox_arr = item['bbox']
                bbox = BoundingBox(
                    x=bbox_arr[0],
                    y=bbox_arr[1],
                    width=bbox_arr[2] - bbox_arr[0],
                    height=bbox_arr[3] - bbox_arr[1]
                )
                ocr_lines.append(OCRLine(text=line_text, confidence=avg_conf, bbox=bbox))

            if not ocr_lines:
                return OCRExtractResponse(
                    raw_text="",
                    language=use_lang,
                    lines=[],
                    avg_confidence=0.0,
                    engine="TESSERACT",
                    processing_time_ms=round((time.time() - start_time) * 1000, 2)
                )

            raw_text = "\n".join([l.text for l in ocr_lines])
            total_avg_conf = round(sum(l.confidence for l in ocr_lines) / len(ocr_lines), 2)
            proc_time = round((time.time() - start_time) * 1000, 2)

            return OCRExtractResponse(
                raw_text=raw_text,
                language=use_lang,
                lines=ocr_lines,
                avg_confidence=total_avg_conf,
                engine="TESSERACT",
                processing_time_ms=proc_time
            )

        except Exception as e:
            return FallbackOCREngine.extract_text(filename, img_size)
