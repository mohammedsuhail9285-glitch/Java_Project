package com.example.lmchecker.storage;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.InputStream;

public interface FileStorageService {
    String storeOriginal(MultipartFile file);
    String storeOriginal(byte[] content, String filename);
    String storeProcessed(byte[] content, String filename);
    String storeCrop(byte[] content, String filename);
    File getFile(String relativePath);
    boolean exists(String relativePath);
    InputStream getInputStream(String relativePath);
}
