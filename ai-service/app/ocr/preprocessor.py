import io
from PIL import Image, ImageOps, ImageEnhance
import numpy as np

try:
    import cv2
    HAS_CV2 = True
except ImportError:
    HAS_CV2 = False

class ImagePreprocessor:
    """
    Image preprocessing for Legal Metrology package labels.
    Combines OpenCV computer vision preprocessing (rotation/skew correction, adaptive thresholding)
    with PIL enhancements for maximum OCR accuracy.
    """

    @staticmethod
    def deskew(image: Image.Image) -> Image.Image:
        """Corrects slight image rotation (skew) up to +/-45 degrees."""
        if not HAS_CV2:
            return image
        try:
            np_img = np.array(image.convert("L"))
            # Invert colors so text is foreground (white)
            thresh = cv2.threshold(np_img, 0, 255, cv2.THRESH_BINARY_INV | cv2.THRESH_OTSU)[1]
            coords = np.column_stack(np.where(thresh > 0))
            if len(coords) < 50:
                return image
            angle = cv2.minAreaRect(coords)[-1]
            if angle < -45:
                angle = -(90 + angle)
            elif angle > 45:
                angle = 90 - angle
            else:
                angle = -angle

            # Only correct if angle is non-trivial but not extreme (> 0.5 and < 45 degrees)
            if 0.5 < abs(angle) < 45.0:
                (h, w) = np_img.shape[:2]
                center = (w // 2, h // 2)
                M = cv2.getRotationMatrix2D(center, angle, 1.0)
                rotated = cv2.warpAffine(np_img, M, (w, h), flags=cv2.INTER_CUBIC, borderMode=cv2.BORDER_REPLICATE)
                return Image.fromarray(rotated)
        except Exception:
            pass
        return image

    @staticmethod
    def preprocess_image(image_bytes: bytes) -> tuple[Image.Image, bytes]:
        image = Image.open(io.BytesIO(image_bytes))

        # 1. Convert to RGB if RGBA/P
        if image.mode not in ('RGB', 'L'):
            image = image.convert('RGB')

        # 2. Deskew / rotation correction
        deskewed = ImagePreprocessor.deskew(image)

        # 3. Grayscale
        gray = ImageOps.grayscale(deskewed)

        # 4. Contrast enhancement
        enhancer = ImageEnhance.Contrast(gray)
        contrast = enhancer.enhance(1.8)

        # 5. Sharpening
        sharp_enhancer = ImageEnhance.Sharpness(contrast)
        sharpened = sharp_enhancer.enhance(1.5)

        # 6. Output buffer
        output_buffer = io.BytesIO()
        sharpened.save(output_buffer, format='PNG')
        processed_bytes = output_buffer.getvalue()

        return sharpened, processed_bytes
