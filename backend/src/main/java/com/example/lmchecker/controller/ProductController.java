package com.example.lmchecker.controller;

import com.example.lmchecker.dto.ApiResponse;
import com.example.lmchecker.dto.BatchIngestResultDto;
import com.example.lmchecker.dto.ProductCreateRequest;
import com.example.lmchecker.dto.ProductDetailDto;
import com.example.lmchecker.entity.Product;
import com.example.lmchecker.ingestion.BatchIngestionService;
import com.example.lmchecker.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.lmchecker.entity.ExtractedField;
import com.example.lmchecker.entity.OCRResult;
import com.example.lmchecker.entity.ProductImage;
import com.example.lmchecker.repository.ExtractedFieldRepository;
import com.example.lmchecker.repository.OCRResultRepository;
import com.example.lmchecker.repository.ProductImageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final BatchIngestionService batchIngestionService;
    private final ExtractedFieldRepository extractedFieldRepository;
    private final OCRResultRepository ocrResultRepository;
    private final ProductImageRepository productImageRepository;

    public ProductController(ProductService productService,
                             BatchIngestionService batchIngestionService,
                             ExtractedFieldRepository extractedFieldRepository,
                             OCRResultRepository ocrResultRepository,
                             ProductImageRepository productImageRepository) {
        this.productService = productService;
        this.batchIngestionService = batchIngestionService;
        this.extractedFieldRepository = extractedFieldRepository;
        this.ocrResultRepository = ocrResultRepository;
        this.productImageRepository = productImageRepository;
    }

    @GetMapping("/{id}/fields")
    public ResponseEntity<List<ExtractedField>> getProductFields(@PathVariable Long id) {
        return ResponseEntity.ok(extractedFieldRepository.findByProductId(id));
    }

    @GetMapping("/{id}/ocr")
    public ResponseEntity<List<OCRResult>> getProductOcr(@PathVariable Long id) {
        List<ProductImage> images = productImageRepository.findByProductId(id);
        List<OCRResult> results = new ArrayList<>();
        for (ProductImage img : images) {
            results.addAll(ocrResultRepository.findByProductImageId(img.getId()));
        }
        return ResponseEntity.ok(results);
    }


    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductDetailDto>>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductDetailDto> results = productService.searchProducts(keyword, categoryId, sellerId, brandId, status, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Products retrieved", results));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailDto>> getProductById(@PathVariable Long id) {
        ProductDetailDto product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.ok("Product details retrieved", product));
    }

    @PostMapping(value = "/manual", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'REGULATOR', 'REVIEWER')")
    public ResponseEntity<ApiResponse<ProductDetailDto>> createProductManual(
            @RequestPart("product") ProductCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        ProductDetailDto created = productService.createProductManual(request, image);
        return ResponseEntity.ok(ApiResponse.ok("Product created and queued for scan", created));
    }

    @PostMapping(value = "/ingest/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'REGULATOR')")
    public ResponseEntity<ApiResponse<BatchIngestResultDto>> ingestCsv(
            @RequestPart("file") MultipartFile file) {
        BatchIngestResultDto result = batchIngestionService.ingestCsv(file);
        return ResponseEntity.ok(ApiResponse.ok("CSV batch ingested successfully", result));
    }

    @PostMapping(value = "/ingest/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'REGULATOR')")
    public ResponseEntity<ApiResponse<BatchIngestResultDto>> ingestJson(
            @RequestPart("file") MultipartFile file) {
        BatchIngestResultDto result = batchIngestionService.ingestJson(file);
        return ResponseEntity.ok(ApiResponse.ok("JSON batch ingested successfully", result));
    }

    @PostMapping("/ingest/url")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGULATOR')")
    public ResponseEntity<ApiResponse<ProductDetailDto>> ingestUrl(@RequestBody Map<String, String> payload) {
        String url = payload.get("url");
        Product product = batchIngestionService.ingestUrl(url);
        ProductDetailDto detail = productService.getProductById(product.getId());
        return ResponseEntity.ok(ApiResponse.ok("Product fetched via crawler simulator", detail));
    }

    @PostMapping("/ingest/crawl")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGULATOR')")
    public ResponseEntity<ApiResponse<BatchIngestResultDto>> ingestSimulatedCrawl(
            @RequestParam(defaultValue = "5") int count) {
        BatchIngestResultDto result = batchIngestionService.ingestSimulatedCrawl(count);
        return ResponseEntity.ok(ApiResponse.ok("Simulated crawl batch completed", result));
    }
}
