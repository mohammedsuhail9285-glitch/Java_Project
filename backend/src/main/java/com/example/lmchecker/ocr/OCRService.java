package com.example.lmchecker.ocr;

public interface OCRService {
    OCRResponseDto extractText(byte[] imageBytes, String filename, String language);
}
