package com.example.lmchecker.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * Stub implementation of FileStorageService for AWS S3 / MinIO object storage.
 * Active profile uses LocalStorageService; this demonstrates modular drop-in replacement.
 */
public class S3StorageServiceStub implements FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(S3StorageServiceStub.class);

    @Override
    public String storeOriginal(MultipartFile file) {
        log.info("[S3 STUB] Uploading original file to S3 bucket: {}", file.getOriginalFilename());
        return "s3://regtech-sentinel/original/" + file.getOriginalFilename();
    }

    @Override
    public String storeOriginal(byte[] content, String filename) {
        log.info("[S3 STUB] Uploading original bytes to S3: {}", filename);
        return "s3://regtech-sentinel/original/" + filename;
    }

    @Override
    public String storeProcessed(byte[] content, String filename) {
        log.info("[S3 STUB] Uploading processed image to S3: {}", filename);
        return "s3://regtech-sentinel/processed/" + filename;
    }

    @Override
    public String storeCrop(byte[] content, String filename) {
        log.info("[S3 STUB] Uploading evidence crop to S3: {}", filename);
        return "s3://regtech-sentinel/crops/" + filename;
    }

    @Override
    public File getFile(String relativePath) {
        throw new UnsupportedOperationException("Direct file handle not supported on remote S3 storage");
    }

    @Override
    public boolean exists(String relativePath) {
        return true;
    }

    @Override
    public InputStream getInputStream(String relativePath) {
        throw new UnsupportedOperationException("Use presigned S3 URL or S3 client stream");
    }
}
