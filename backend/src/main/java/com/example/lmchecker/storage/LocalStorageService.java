package com.example.lmchecker.storage;

import com.example.lmchecker.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalStorageService implements FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalStorageService.class);

    private final Path rootPath;
    private final Path originalPath;
    private final Path processedPath;
    private final Path cropsPath;

    public LocalStorageService(@Value("${app.storage.upload-dir:./storage/uploads}") String uploadDir) {
        this.rootPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.originalPath = this.rootPath.resolve("original");
        this.processedPath = this.rootPath.resolve("processed");
        this.cropsPath = this.rootPath.resolve("crops");

        try {
            Files.createDirectories(this.originalPath);
            Files.createDirectories(this.processedPath);
            Files.createDirectories(this.cropsPath);
        } catch (IOException e) {
            log.error("Could not initialize storage directories: {}", e.getMessage(), e);
        }
    }

    @Override
    public String storeOriginal(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty");
        }
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.png");
        String sanitizedName = UUID.randomUUID() + "_" + originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

        try {
            Path targetLocation = this.originalPath.resolve(sanitizedName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "storage/uploads/original/" + sanitizedName;
        } catch (IOException e) {
            throw new BadRequestException("Failed to store uploaded file: " + e.getMessage());
        }
    }

    @Override
    public String storeOriginal(byte[] content, String filename) {
        String sanitizedName = UUID.randomUUID() + "_" + filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        try {
            Path targetLocation = this.originalPath.resolve(sanitizedName);
            Files.write(targetLocation, content);
            return "storage/uploads/original/" + sanitizedName;
        } catch (IOException e) {
            throw new BadRequestException("Failed to store original image bytes: " + e.getMessage());
        }
    }

    @Override
    public String storeProcessed(byte[] content, String filename) {
        String sanitizedName = "proc_" + UUID.randomUUID() + "_" + filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        try {
            Path targetLocation = this.processedPath.resolve(sanitizedName);
            Files.write(targetLocation, content);
            return "storage/uploads/processed/" + sanitizedName;
        } catch (IOException e) {
            throw new BadRequestException("Failed to store processed image: " + e.getMessage());
        }
    }

    @Override
    public String storeCrop(byte[] content, String filename) {
        String sanitizedName = "crop_" + UUID.randomUUID() + "_" + filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        try {
            Path targetLocation = this.cropsPath.resolve(sanitizedName);
            Files.write(targetLocation, content);
            return "storage/uploads/crops/" + sanitizedName;
        } catch (IOException e) {
            throw new BadRequestException("Failed to store cropped evidence: " + e.getMessage());
        }
    }

    @Override
    public File getFile(String relativePath) {
        // Handle various relative path formats
        String normalized = relativePath.replace("storage/uploads/", "");
        return this.rootPath.resolve(normalized).toFile();
    }

    @Override
    public boolean exists(String relativePath) {
        return getFile(relativePath).exists();
    }

    @Override
    public InputStream getInputStream(String relativePath) {
        try {
            return new FileInputStream(getFile(relativePath));
        } catch (FileNotFoundException e) {
            throw new BadRequestException("File not found: " + relativePath);
        }
    }
}
