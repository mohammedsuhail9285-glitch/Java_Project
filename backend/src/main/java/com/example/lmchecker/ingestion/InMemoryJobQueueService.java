package com.example.lmchecker.ingestion;

import com.example.lmchecker.ai.AIExtractionService;
import com.example.lmchecker.ai.ExtractedFieldDto;
import com.example.lmchecker.compliance.ProductComplianceContext;
import com.example.lmchecker.compliance.RuleExecutionEngine;
import com.example.lmchecker.entity.*;
import com.example.lmchecker.ocr.OCRResponseDto;
import com.example.lmchecker.ocr.OCRService;
import com.example.lmchecker.repository.*;
import com.example.lmchecker.storage.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class InMemoryJobQueueService implements JobQueueService {

    private static final Logger log = LoggerFactory.getLogger(InMemoryJobQueueService.class);

    private final ScanJobRepository scanJobRepository;
    private final AsyncScanPipelineExecutor asyncPipelineExecutor;

    public InMemoryJobQueueService(ScanJobRepository scanJobRepository, @Lazy AsyncScanPipelineExecutor asyncPipelineExecutor) {
        this.scanJobRepository = scanJobRepository;
        this.asyncPipelineExecutor = asyncPipelineExecutor;
    }

    @Override
    public void enqueueScanJob(Long scanJobId) {
        log.info("Enqueueing ScanJob ID: {}", scanJobId);
        asyncPipelineExecutor.executeAsyncScan(scanJobId);
    }

    @Override
    @Transactional
    public void cancelJob(Long scanJobId) {
        scanJobRepository.findById(scanJobId).ifPresent(job -> {
            job.setStatus(ScanJobStatus.FAILED);
            job.setErrorMessage("Cancelled by user");
            job.setCompletedAt(LocalDateTime.now());
            scanJobRepository.save(job);
            log.info("Cancelled ScanJob ID: {}", scanJobId);
        });
    }

    @Service
    public static class AsyncScanPipelineExecutor {
        private static final Logger log = LoggerFactory.getLogger(AsyncScanPipelineExecutor.class);

        private final ScanJobRepository scanJobRepository;
        private final ProductRepository productRepository;
        private final ProductImageRepository productImageRepository;
        private final ProductListingRepository productListingRepository;
        private final OCRResultRepository ocrResultRepository;
        private final ExtractedFieldRepository extractedFieldRepository;
        private final ScanResultRepository scanResultRepository;
        private final OCRService ocrService;
        private final AIExtractionService aiExtractionService;
        private final RuleExecutionEngine ruleExecutionEngine;
        private final FileStorageService fileStorageService;

        public AsyncScanPipelineExecutor(ScanJobRepository scanJobRepository,
                                         ProductRepository productRepository,
                                         ProductImageRepository productImageRepository,
                                         ProductListingRepository productListingRepository,
                                         OCRResultRepository ocrResultRepository,
                                         ExtractedFieldRepository extractedFieldRepository,
                                         ScanResultRepository scanResultRepository,
                                         OCRService ocrService,
                                         AIExtractionService aiExtractionService,
                                         RuleExecutionEngine ruleExecutionEngine,
                                         FileStorageService fileStorageService) {
            this.scanJobRepository = scanJobRepository;
            this.productRepository = productRepository;
            this.productImageRepository = productImageRepository;
            this.productListingRepository = productListingRepository;
            this.ocrResultRepository = ocrResultRepository;
            this.extractedFieldRepository = extractedFieldRepository;
            this.scanResultRepository = scanResultRepository;
            this.ocrService = ocrService;
            this.aiExtractionService = aiExtractionService;
            this.ruleExecutionEngine = ruleExecutionEngine;
            this.fileStorageService = fileStorageService;
        }

        @Async
        @Transactional
        public void executeAsyncScan(Long scanJobId) {
            log.info("Starting end-to-end background execution for ScanJob ID: {}", scanJobId);
            ScanJob job = scanJobRepository.findById(scanJobId).orElse(null);
            if (job == null) return;

            try {
                // Step 1: Processing initialization
                job.setStatus(ScanJobStatus.PROCESSING);
                job.setProgressPct(15);
                scanJobRepository.save(job);

                // Select target products
                List<Product> targetProducts = new ArrayList<>();
                if (job.getProductId() != null) {
                    productRepository.findById(job.getProductId()).ifPresent(targetProducts::add);
                } else {
                    targetProducts = productRepository.findAll();
                }

                if (targetProducts.isEmpty()) {
                    job.setStatus(ScanJobStatus.COMPLETED);
                    job.setProgressPct(100);
                    job.setCompletedAt(LocalDateTime.now());
                    scanJobRepository.save(job);
                    return;
                }

                int total = targetProducts.size();
                int processed = 0;

                for (Product product : targetProducts) {
                    // Update job status to OCR_PROCESSING
                    job.setStatus(ScanJobStatus.OCR_PROCESSING);
                    job.setProgressPct(20 + (int) (((double) processed / total) * 30));
                    scanJobRepository.save(job);

                    List<ProductImage> allImages = productImageRepository.findByProductId(product.getId());
                    List<ExtractedField> savedFields = new ArrayList<>();
                    OCRResult ocrEntity = null;
                    ProductImage primaryLabelImg = null;

                    // 1. Process Listing Screenshot (if present)
                    ProductImage screenshotImg = allImages.stream()
                            .filter(im -> "LISTING_SCREENSHOT".equalsIgnoreCase(im.getImageType()))
                            .findFirst().orElse(null);

                    if (screenshotImg != null) {
                        byte[] screenBytes = loadImageBytes(screenshotImg.getOriginalPath(), product.getName());
                        OCRResponseDto screenOcr = ocrService.extractText(screenBytes, screenshotImg.getOriginalPath(), "eng");
                        if (screenOcr != null && screenOcr.getRawText() != null && !screenOcr.getRawText().isBlank()) {
                            if (product.getDescription() == null || product.getDescription().isBlank()) {
                                product.setDescription(screenOcr.getRawText());
                            }
                            List<ExtractedFieldDto> screenFields = aiExtractionService.extractFieldsFromOCR(screenOcr);
                            for (ExtractedFieldDto dto : screenFields) {
                                FieldType ft = mapToFieldType(dto.getFieldType());
                                if (ft != null) {
                                    ExtractedField ef = ExtractedField.builder()
                                            .productId(product.getId())
                                            .fieldType(ft)
                                            .rawValue(dto.getRawValue())
                                            .normalizedValue(dto.getNormalizedValue())
                                            .unit(dto.getUnit())
                                            .source(FieldSource.LISTING)
                                            .confidence(dto.getConfidence())
                                            .createdAt(LocalDateTime.now())
                                            .build();
                                    savedFields.add(extractedFieldRepository.save(ef));

                                    if (ft == FieldType.MRP && (product.getDeclaredMrp() == null || product.getDeclaredMrp() <= 0)) {
                                        try {
                                            double p = Double.parseDouble(dto.getNormalizedValue());
                                            product.setDeclaredMrp(p);
                                            if (product.getListingPrice() == null || product.getListingPrice() <= 0) {
                                                product.setListingPrice(p);
                                            }
                                        } catch (Exception ignored) {}
                                    }
                                }
                            }
                        }
                    }

                    // 2. Process Digital Listing Text (if no screenshot fields or if listing text present)
                    ProductListing listing = productListingRepository
                            .findFirstByProductIdOrderByScrapedAtDesc(product.getId())
                            .orElse(null);

                    String listingText = listing != null && listing.getRawExtractedFields() != null && !listing.getRawExtractedFields().isBlank()
                            ? listing.getRawExtractedFields()
                            : product.getDescription();

                    boolean hasListingFields = savedFields.stream().anyMatch(f -> f.getSource() == FieldSource.LISTING);
                    if (!hasListingFields && listingText != null && !listingText.isBlank()) {
                        List<ExtractedFieldDto> listDtos = aiExtractionService.extractFieldsFromText(listingText, "LISTING");
                        for (ExtractedFieldDto dto : listDtos) {
                            FieldType ft = mapToFieldType(dto.getFieldType());
                            if (ft != null) {
                                ExtractedField ef = ExtractedField.builder()
                                        .productId(product.getId())
                                        .fieldType(ft)
                                        .rawValue(dto.getRawValue())
                                        .normalizedValue(dto.getNormalizedValue())
                                        .unit(dto.getUnit())
                                        .source(FieldSource.LISTING)
                                        .confidence(dto.getConfidence())
                                        .createdAt(LocalDateTime.now())
                                        .build();
                                savedFields.add(extractedFieldRepository.save(ef));

                                if (ft == FieldType.MRP && (product.getDeclaredMrp() == null || product.getDeclaredMrp() <= 0)) {
                                    try {
                                        double p = Double.parseDouble(dto.getNormalizedValue());
                                        product.setDeclaredMrp(p);
                                        if (product.getListingPrice() == null || product.getListingPrice() <= 0) {
                                            product.setListingPrice(p);
                                        }
                                    } catch (Exception ignored) {}
                                }
                                if (ft == FieldType.COUNTRY_OF_ORIGIN && (product.getCountryOfOrigin() == null || product.getCountryOfOrigin().isBlank())) {
                                    product.setCountryOfOrigin(dto.getNormalizedValue());
                                }
                            }
                        }
                    }

                    // Save updated product if attributes were populated from listing
                    product = productRepository.save(product);

                    // 3. Process Physical Package Label Image (if uploaded)
                    primaryLabelImg = allImages.stream()
                            .filter(im -> !"LISTING_SCREENSHOT".equalsIgnoreCase(im.getImageType()))
                            .findFirst().orElse(null);

                    if (primaryLabelImg != null) {
                        byte[] imgBytes = loadImageBytes(primaryLabelImg.getOriginalPath(), product.getName());
                        OCRResponseDto ocrResponse = ocrService.extractText(imgBytes, primaryLabelImg.getOriginalPath(), "eng");

                        ocrEntity = OCRResult.builder()
                                .productImageId(primaryLabelImg.getId())
                                .rawText(ocrResponse.getRawText())
                                .language(ocrResponse.getLanguage())
                                .confidence(ocrResponse.getAvgConfidence())
                                .engine(ocrResponse.getEngine())
                                .processedAt(LocalDateTime.now())
                                .build();
                        ocrEntity = ocrResultRepository.save(ocrEntity);

                        List<ExtractedFieldDto> extractedDtos = aiExtractionService.extractFieldsFromOCR(ocrResponse);
                        for (ExtractedFieldDto dto : extractedDtos) {
                            FieldType ft = mapToFieldType(dto.getFieldType());
                            if (ft != null) {
                                String bboxStr = null;
                                if (dto.getBbox() != null) {
                                    bboxStr = String.format("{\"x\":%d,\"y\":%d,\"width\":%d,\"height\":%d}",
                                            dto.getBbox().getX(), dto.getBbox().getY(),
                                            dto.getBbox().getWidth(), dto.getBbox().getHeight());
                                }

                                ExtractedField ef = ExtractedField.builder()
                                        .productId(product.getId())
                                        .fieldType(ft)
                                        .rawValue(dto.getRawValue())
                                        .normalizedValue(dto.getNormalizedValue())
                                        .unit(dto.getUnit())
                                        .source(FieldSource.IMAGE)
                                        .confidence(dto.getConfidence())
                                        .boundingBox(bboxStr)
                                        .createdAt(LocalDateTime.now())
                                        .build();
                                savedFields.add(extractedFieldRepository.save(ef));
                            }
                        }
                    }

                    // 4. Build Compliance Context & Evaluate Rules
                    job.setStatus(ScanJobStatus.VALIDATING);
                    job.setProgressPct(50 + (int) (((double) processed / total) * 45));
                    scanJobRepository.save(job);

                    ProductComplianceContext ctx = new ProductComplianceContext(
                            product, listing, primaryLabelImg, ocrEntity, savedFields
                    );

                    ComplianceCheck check = ruleExecutionEngine.evaluateProduct(ctx, scanJobId);

                    // 5. Save ScanResult
                    ScanResult scanResult = ScanResult.builder()
                            .scanJobId(scanJobId)
                            .productId(product.getId())
                            .summary(check.getSummaryNotes())
                            .createdAt(LocalDateTime.now())
                            .build();
                    scanResultRepository.save(scanResult);

                    processed++;
                }

                // Step 4: Completed
                job.setStatus(ScanJobStatus.COMPLETED);
                job.setProgressPct(100);
                job.setCompletedAt(LocalDateTime.now());
                scanJobRepository.save(job);
                log.info("ScanJob ID: {} completed successfully. Processed {} products.", scanJobId, total);

            } catch (Exception e) {
                log.error("ScanJob ID: {} failed: {}", scanJobId, e.getMessage(), e);
                job.setStatus(ScanJobStatus.FAILED);
                job.setErrorMessage(e.getMessage());
                job.setCompletedAt(LocalDateTime.now());
                scanJobRepository.save(job);
            }
        }

        private byte[] loadImageBytes(String imagePath, String productName) {
            try {
                if (imagePath != null) {
                    if (fileStorageService.exists(imagePath)) {
                        return Files.readAllBytes(fileStorageService.getFile(imagePath).toPath());
                    }
                    Path[] candidates = new Path[]{
                        Paths.get(imagePath),
                        Paths.get("storage/uploads", imagePath),
                        Paths.get("storage/uploads/original", imagePath),
                        Paths.get("../demo-data", imagePath),
                        Paths.get("demo-data", imagePath),
                        Paths.get("../test-data", imagePath),
                        Paths.get("test-data", imagePath),
                        Paths.get("backend/storage/uploads", imagePath),
                        Paths.get("d:/JAVA_PROJECT/demo-data", imagePath),
                        Paths.get("d:/JAVA_PROJECT/test-data", imagePath)
                    };
                    for (Path p : candidates) {
                        if (Files.exists(p) && !Files.isDirectory(p)) {
                            return Files.readAllBytes(p);
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("Failed reading file {}: {}", imagePath, e.getMessage());
            }
            // Return synthetic placeholder bytes if disk image not found
            return ("LEGAL METROLOGY DECLARATION: " + productName).getBytes();
        }

        private FieldType mapToFieldType(String name) {
            if (name == null) return null;
            try {
                return FieldType.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                if ("PRICE".equalsIgnoreCase(name)) return FieldType.MRP;
                if ("QUANTITY".equalsIgnoreCase(name)) return FieldType.NET_QTY;
                if ("ORIGIN".equalsIgnoreCase(name)) return FieldType.COUNTRY_OF_ORIGIN;
                if ("DATE".equalsIgnoreCase(name)) return FieldType.MFG_DATE;
                return null;
            }
        }
    }
}
