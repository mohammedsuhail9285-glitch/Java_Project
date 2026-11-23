package com.example.lmchecker.controller;

import com.example.lmchecker.dto.ApiResponse;
import com.example.lmchecker.dto.BatchIngestResultDto;
import com.example.lmchecker.dto.ProductCreateRequest;
import com.example.lmchecker.dto.ScanJobDto;
import com.example.lmchecker.entity.Product;
import com.example.lmchecker.entity.ScanJob;
import com.example.lmchecker.entity.ScanJobStatus;
import com.example.lmchecker.exception.BadRequestException;
import com.example.lmchecker.ingestion.BatchIngestionService;
import com.example.lmchecker.repository.ScanJobRepository;
import com.example.lmchecker.storage.FileStorageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ingestion")
public class IngestionController {

    private final BatchIngestionService batchIngestionService;
    private final ScanJobRepository scanJobRepository;
    private final FileStorageService fileStorageService;

    public IngestionController(BatchIngestionService batchIngestionService,
                               ScanJobRepository scanJobRepository,
                               FileStorageService fileStorageService) {
        this.batchIngestionService = batchIngestionService;
        this.scanJobRepository = scanJobRepository;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/crawl-sandbox")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGULATOR', 'REVIEWER', 'ANALYST')")
    public ResponseEntity<ApiResponse<ScanJobDto>> crawlSandbox(@RequestBody(required = false) Map<String, Object> payload) {
        int batchSize = 5;
        if (payload != null && payload.containsKey("batchSize")) {
            try {
                batchSize = Integer.parseInt(payload.get("batchSize").toString());
            } catch (Exception ignored) {}
        }
        String platform = (payload != null && payload.containsKey("platform")) ? payload.get("platform").toString() : "RetailBharat";

        BatchIngestResultDto batchResult = batchIngestionService.ingestSimulatedCrawl(batchSize);

        List<ScanJob> recentJobs = scanJobRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 1)).getContent();
        Long jobId = !recentJobs.isEmpty() ? recentJobs.get(0).getId() : System.currentTimeMillis();

        ScanJobDto jobDto = new ScanJobDto();
        jobDto.setId(jobId);
        jobDto.setStatus(ScanJobStatus.COMPLETED);
        jobDto.setProgressPct(100);
        jobDto.setBatchId(batchResult.getBatchId());
        jobDto.setTotalItems(batchResult.getTotalProcessed());
        jobDto.setProcessedItems(batchResult.getSuccessCount());
        jobDto.setJobType("SANDBOX_CRAWL_" + platform.toUpperCase());
        jobDto.setCreatedAt(LocalDateTime.now());
        jobDto.setCompletedAt(LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.ok("Sandbox simulated crawl initiated successfully", jobDto));
    }

    private static final java.util.regex.Pattern URL_PATTERN =
        java.util.regex.Pattern.compile("^(https?://)([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})(:[0-9]+)?(/.*)?$", java.util.regex.Pattern.CASE_INSENSITIVE);

    @PostMapping("/url")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGULATOR', 'REVIEWER', 'ANALYST')")
    public ResponseEntity<ApiResponse<ScanJobDto>> ingestUrl(@RequestBody Map<String, String> payload) {
        String url = payload != null ? payload.get("url") : null;
        if (url == null || url.isBlank()) {
            throw new BadRequestException("Product URL is required for crawling");
        }
        if (!URL_PATTERN.matcher(url.trim()).matches()) {
            throw new BadRequestException("Invalid URL format. URL must start with http:// or https:// and include a valid domain name.");
        }

        Product product = batchIngestionService.ingestUrl(url.trim());

        List<ScanJob> jobs = scanJobRepository.findByProductIdOrderByCreatedAtDesc(product.getId());
        ScanJob job = !jobs.isEmpty() ? jobs.get(0) : null;
        Long jobId = job != null ? job.getId() : product.getId();

        ScanJobDto jobDto = new ScanJobDto();
        jobDto.setId(jobId);
        jobDto.setProductId(product.getId());
        jobDto.setStatus(job != null ? job.getStatus() : ScanJobStatus.COMPLETED);
        jobDto.setProgressPct(job != null ? job.getProgressPct() : 100);
        jobDto.setTotalItems(1);
        jobDto.setProcessedItems(1);
        jobDto.setJobType("URL_FETCH");
        jobDto.setCreatedAt(LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.ok("Product listing fetched and queued for automated check", jobDto));
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'REGULATOR', 'REVIEWER', 'ANALYST')")
    public ResponseEntity<ApiResponse<ScanJobDto>> ingestImage(
            @RequestPart("image") MultipartFile image,
            @RequestParam(value = "productName", required = false) String productName) {
        if (image == null || image.isEmpty()) {
            throw new BadRequestException("Image file is required for OCR scanning.");
        }
        validateImageFile(image);

        String filename = image.getOriginalFilename() != null ? image.getOriginalFilename() : "package_label.png";
        String name = (productName != null && !productName.isBlank())
            ? productName.trim()
            : "Label Scan - " + filename.replaceAll("[^a-zA-Z0-9.-]", "_");

        String storedPath = fileStorageService.storeOriginal(image);

        ProductCreateRequest req = new ProductCreateRequest();
        req.setName(name);
        req.setDeclaredMrp(199.0); // Baseline placeholder before OCR extraction
        req.setListingPrice(199.0);
        req.setCountryOfOrigin("India");
        req.setIngestionSource("IMAGE_OCR_SCAN");

        Product product = batchIngestionService.ingestManual(req, storedPath);

        List<ScanJob> jobs = scanJobRepository.findByProductIdOrderByCreatedAtDesc(product.getId());
        ScanJob job = !jobs.isEmpty() ? jobs.get(0) : null;
        Long jobId = job != null ? job.getId() : product.getId();

        ScanJobDto jobDto = new ScanJobDto();
        jobDto.setId(jobId);
        jobDto.setProductId(product.getId());
        jobDto.setStatus(job != null ? job.getStatus() : ScanJobStatus.COMPLETED);
        jobDto.setProgressPct(job != null ? job.getProgressPct() : 100);
        jobDto.setTotalItems(1);
        jobDto.setProcessedItems(1);
        jobDto.setJobType("IMAGE_OCR_SCAN");
        jobDto.setCreatedAt(LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.ok("Package label image received and queued for OCR & compliance scan", jobDto));
    }

    @PostMapping(value = "/dual-scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'REGULATOR', 'REVIEWER', 'ANALYST')")
    public ResponseEntity<ApiResponse<ScanJobDto>> dualScan(
            @RequestPart(value = "labelImage", required = false) MultipartFile labelImage,
            @RequestPart(value = "listingScreenshot", required = false) MultipartFile listingScreenshot,
            @RequestParam(value = "listingText", required = false) String listingText,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "productUrl", required = false) String productUrl,
            @RequestParam(value = "declaredMrp", required = false) Double declaredMrp,
            @RequestParam(value = "listingPrice", required = false) Double listingPrice,
            @RequestParam(value = "countryOfOrigin", required = false) String countryOfOrigin) {

        if ((labelImage == null || labelImage.isEmpty()) &&
            (listingScreenshot == null || listingScreenshot.isEmpty()) &&
            (listingText == null || listingText.isBlank())) {
            throw new BadRequestException("Please provide at least a package label image, listing screenshot, or listing text.");
        }

        String labelPath = null;
        if (labelImage != null && !labelImage.isEmpty()) {
            validateImageFile(labelImage);
            labelPath = fileStorageService.storeOriginal(labelImage);
        }

        String screenPath = null;
        if (listingScreenshot != null && !listingScreenshot.isEmpty()) {
            validateImageFile(listingScreenshot);
            screenPath = fileStorageService.storeOriginal(listingScreenshot);
        }

        String resolvedName = productName;
        if (resolvedName == null || resolvedName.isBlank()) {
            if (labelImage != null && !labelImage.isEmpty()) {
                resolvedName = "Package Scan - " + labelImage.getOriginalFilename();
            } else {
                resolvedName = "Digital Listing Inspection";
            }
        }

        Product product = batchIngestionService.ingestDual(
                resolvedName,
                listingText,
                labelPath,
                screenPath,
                productUrl,
                declaredMrp,
                listingPrice,
                countryOfOrigin
        );

        List<ScanJob> jobs = scanJobRepository.findByProductIdOrderByCreatedAtDesc(product.getId());
        ScanJob job = !jobs.isEmpty() ? jobs.get(0) : null;
        Long jobId = job != null ? job.getId() : product.getId();

        ScanJobDto jobDto = new ScanJobDto();
        jobDto.setId(jobId);
        jobDto.setProductId(product.getId());
        jobDto.setStatus(job != null ? job.getStatus() : ScanJobStatus.COMPLETED);
        jobDto.setProgressPct(job != null ? job.getProgressPct() : 100);
        jobDto.setTotalItems(1);
        jobDto.setProcessedItems(1);
        jobDto.setJobType("DUAL_SOURCE_SCAN");
        jobDto.setCreatedAt(LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.ok("Dual-source compliance scan queued successfully", jobDto));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'REGULATOR', 'REVIEWER', 'ANALYST')")
    public ResponseEntity<ApiResponse<ScanJobDto>> uploadBatchFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "CSV") String type) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Batch upload file cannot be empty.");
        }
        if (file.getSize() > 25 * 1024 * 1024) {
            throw new BadRequestException("Batch file size exceeds maximum limit of 25MB.");
        }

        String origName = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        BatchIngestResultDto result;
        if ("JSON".equalsIgnoreCase(type)) {
            if (!origName.endsWith(".json")) {
                throw new BadRequestException("Invalid file format for JSON batch. File must have a .json extension.");
            }
            result = batchIngestionService.ingestJson(file);
        } else {
            if (!origName.endsWith(".csv")) {
                throw new BadRequestException("Invalid file format for CSV batch. File must have a .csv extension.");
            }
            result = batchIngestionService.ingestCsv(file);
        }

        List<ScanJob> recentJobs = scanJobRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 1)).getContent();
        Long jobId = !recentJobs.isEmpty() ? recentJobs.get(0).getId() : System.currentTimeMillis();

        ScanJobDto jobDto = new ScanJobDto();
        jobDto.setId(jobId);
        jobDto.setStatus(ScanJobStatus.COMPLETED);
        jobDto.setProgressPct(100);
        jobDto.setBatchId(result.getBatchId());
        jobDto.setTotalItems(result.getTotalProcessed());
        jobDto.setProcessedItems(result.getSuccessCount());
        jobDto.setJobType("BATCH_UPLOAD_" + type.toUpperCase());
        jobDto.setCreatedAt(LocalDateTime.now());
        jobDto.setCompletedAt(LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.ok("Batch catalog file uploaded and queued successfully", jobDto));
    }

    @PostMapping(value = "/manual", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyRole('ADMIN', 'REGULATOR', 'REVIEWER', 'ANALYST')")
    public ResponseEntity<ApiResponse<ScanJobDto>> ingestManual(
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "declaredMrp", required = false) Double declaredMrp,
            @RequestParam(value = "listingPrice", required = false) Double listingPrice,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "countryOfOrigin", required = false) String countryOfOrigin,
            @RequestParam(value = "productUrl", required = false) String productUrl,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "sellerId", required = false) Long sellerId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestBody(required = false) ProductCreateRequest jsonRequest) {

        ProductCreateRequest req = new ProductCreateRequest();
        if (jsonRequest != null) {
            req = jsonRequest;
        } else {
            req.setName(productName);
            req.setDeclaredMrp(declaredMrp);
            req.setListingPrice(listingPrice != null ? listingPrice : declaredMrp);
            req.setDescription(description);
            req.setCountryOfOrigin(countryOfOrigin != null && !countryOfOrigin.isBlank() ? countryOfOrigin : "India");
            req.setProductUrl(productUrl);
            req.setBrandId(brandId);
            req.setSellerId(sellerId);
            req.setCategoryId(categoryId);
        }

        if (req.getName() == null || req.getName().trim().isEmpty()) {
            throw new BadRequestException("Commodity name is required.");
        }
        if (req.getDeclaredMrp() == null || req.getDeclaredMrp() <= 0) {
            throw new BadRequestException("Declared MRP is required and must be greater than 0.");
        }
        if (req.getListingPrice() == null || req.getListingPrice() <= 0) {
            req.setListingPrice(req.getDeclaredMrp());
        }

        MultipartFile imgFile = file != null ? file : image;
        String storedPath = null;
        if (imgFile != null && !imgFile.isEmpty()) {
            validateImageFile(imgFile);
            storedPath = fileStorageService.storeOriginal(imgFile);
        }

        Product product = batchIngestionService.ingestManual(req, storedPath);

        List<ScanJob> jobs = scanJobRepository.findByProductIdOrderByCreatedAtDesc(product.getId());
        ScanJob job = !jobs.isEmpty() ? jobs.get(0) : null;
        Long jobId = job != null ? job.getId() : product.getId();

        ScanJobDto jobDto = new ScanJobDto();
        jobDto.setId(jobId);
        jobDto.setProductId(product.getId());
        jobDto.setStatus(job != null ? job.getStatus() : ScanJobStatus.COMPLETED);
        jobDto.setProgressPct(job != null ? job.getProgressPct() : 100);
        jobDto.setTotalItems(1);
        jobDto.setProcessedItems(1);
        jobDto.setJobType("MANUAL_ENTRY");
        jobDto.setCreatedAt(LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.ok("Product ingested and queued for scan", jobDto));
    }

    private void validateImageFile(MultipartFile file) {
        if (file.getSize() > 15 * 1024 * 1024) {
            throw new BadRequestException("Image file size exceeds maximum limit of 15MB.");
        }
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        String contentType = file.getContentType() != null ? file.getContentType().toLowerCase() : "";
        boolean validExt = filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png") || filename.endsWith(".webp");
        boolean validMime = contentType.startsWith("image/");
        if (!validExt && !validMime) {
            throw new BadRequestException("Invalid image file format. Only JPEG, PNG, and WebP images are supported.");
        }
    }
}
