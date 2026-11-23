package com.example.lmchecker.ingestion;

import com.example.lmchecker.dto.BatchIngestResultDto;
import com.example.lmchecker.dto.ProductCreateRequest;
import com.example.lmchecker.entity.*;
import com.example.lmchecker.exception.BadRequestException;
import com.example.lmchecker.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BatchIngestionService {

    private static final Logger log = LoggerFactory.getLogger(BatchIngestionService.class);

    private final ProductRepository productRepository;
    private final ProductListingRepository productListingRepository;
    private final ProductImageRepository productImageRepository;
    private final BrandRepository brandRepository;
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;
    private final ScanJobRepository scanJobRepository;
    private final JobQueueService jobQueueService;
    private final MockProductDataSource mockProductDataSource;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BatchIngestionService(
            ProductRepository productRepository,
            ProductListingRepository productListingRepository,
            ProductImageRepository productImageRepository,
            BrandRepository brandRepository,
            SellerRepository sellerRepository,
            CategoryRepository categoryRepository,
            ScanJobRepository scanJobRepository,
            JobQueueService jobQueueService,
            MockProductDataSource mockProductDataSource) {
        this.productRepository = productRepository;
        this.productListingRepository = productListingRepository;
        this.productImageRepository = productImageRepository;
        this.brandRepository = brandRepository;
        this.sellerRepository = sellerRepository;
        this.categoryRepository = categoryRepository;
        this.scanJobRepository = scanJobRepository;
        this.jobQueueService = jobQueueService;
        this.mockProductDataSource = mockProductDataSource;
    }

    @Transactional
    public Product ingestManual(ProductCreateRequest request, String storedImagePath) {
        Long brandId = resolveBrandId(request.getBrandName(), request.getBrandId());
        Long sellerId = resolveSellerId(request.getSellerName(), request.getSellerId());
        Long categoryId = resolveCategoryId(request.getCategoryName(), request.getCategoryId());

        Product product = Product.builder()
            .name(request.getName())
            .brandId(brandId)
            .sellerId(sellerId)
            .categoryId(categoryId)
            .declaredMrp(request.getDeclaredMrp())
            .listingPrice(request.getListingPrice() != null ? request.getListingPrice() : request.getDeclaredMrp())
            .description(request.getDescription())
            .countryOfOrigin(request.getCountryOfOrigin())
            .productUrl(request.getProductUrl())
            .ingestionSource("MANUAL")
            .status("QUEUED_FOR_SCAN")
            .build();

        Product savedProduct = productRepository.save(product);

        // Save raw listing record
        String rawFields = serializeMap(request.getListingAttributes());
        ProductListing listing = ProductListing.builder()
            .productId(savedProduct.getId())
            .rawExtractedFields(rawFields)
            .sourceType("MANUAL_FORM")
            .build();
        productListingRepository.save(listing);

        // Link package label image if uploaded
        if (storedImagePath != null && !storedImagePath.isBlank()) {
            ProductImage productImage = ProductImage.builder()
                .productId(savedProduct.getId())
                .originalPath(storedImagePath)
                .imageType("PACKAGE_LABEL")
                .build();
            productImageRepository.save(productImage);
        }

        // Enqueue ScanJob for this product
        ScanJob scanJob = ScanJob.builder()
            .productId(savedProduct.getId())
            .status(ScanJobStatus.QUEUED)
            .startedAt(LocalDateTime.now())
            .build();
        ScanJob savedJob = scanJobRepository.save(scanJob);

        jobQueueService.enqueueScanJob(savedJob.getId());
        return savedProduct;
    }

    @Transactional
    public Product ingestDual(String productName, String listingText, String labelImagePath, String screenshotPath, String productUrl, Double declaredMrp, Double listingPrice, String countryOfOrigin) {
        Long brandId = resolveBrandId(null, 1L);
        Long sellerId = resolveSellerId(null, 1L);
        Long categoryId = resolveCategoryId(null, 1L);

        Double mrp = declaredMrp != null ? declaredMrp : (listingPrice != null ? listingPrice : 0.0);
        Double price = listingPrice != null ? listingPrice : mrp;
        String origin = (countryOfOrigin != null && !countryOfOrigin.isBlank()) ? countryOfOrigin : "India";
        String name = (productName != null && !productName.isBlank()) ? productName : "Dual Source Compliance Inspection";

        Product product = Product.builder()
            .name(name)
            .brandId(brandId)
            .sellerId(sellerId)
            .categoryId(categoryId)
            .declaredMrp(mrp)
            .listingPrice(price)
            .description(listingText)
            .countryOfOrigin(origin)
            .productUrl(productUrl)
            .ingestionSource("DUAL_SCAN")
            .status("QUEUED_FOR_SCAN")
            .build();

        Product savedProduct = productRepository.save(product);

        ProductListing listing = ProductListing.builder()
            .productId(savedProduct.getId())
            .rawExtractedFields(listingText != null ? listingText : "")
            .sourceType(screenshotPath != null ? "SCREENSHOT_OCR" : (listingText != null ? "LISTING_TEXT" : "MANUAL"))
            .scrapedAt(LocalDateTime.now())
            .build();
        productListingRepository.save(listing);

        if (labelImagePath != null && !labelImagePath.isBlank()) {
            ProductImage labelImg = ProductImage.builder()
                .productId(savedProduct.getId())
                .originalPath(labelImagePath)
                .imageType("PACKAGE_LABEL")
                .uploadedAt(LocalDateTime.now())
                .build();
            productImageRepository.save(labelImg);
        }

        if (screenshotPath != null && !screenshotPath.isBlank()) {
            ProductImage screenImg = ProductImage.builder()
                .productId(savedProduct.getId())
                .originalPath(screenshotPath)
                .imageType("LISTING_SCREENSHOT")
                .uploadedAt(LocalDateTime.now())
                .build();
            productImageRepository.save(screenImg);
        }

        ScanJob scanJob = ScanJob.builder()
            .productId(savedProduct.getId())
            .status(ScanJobStatus.QUEUED)
            .startedAt(LocalDateTime.now())
            .build();
        ScanJob savedJob = scanJobRepository.save(scanJob);

        jobQueueService.enqueueScanJob(savedJob.getId());
        return savedProduct;
    }

    @Transactional
    public BatchIngestResultDto ingestCsv(MultipartFile file) {
        String batchId = "BATCH-CSV-" + System.currentTimeMillis();
        List<String> errors = new ArrayList<>();
        int success = 0;
        int total = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            CSVFormat format = CSVFormat.Builder.create()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build();

            CSVParser parser = format.parse(reader);
            for (CSVRecord record : parser) {
                total++;
                try {
                    String name = record.isSet("name") ? record.get("name") : record.get(0);
                    String brand = record.isSet("brand") ? record.get("brand") : "Generic";
                    String seller = record.isSet("seller") ? record.get("seller") : "Marketplace Seller";
                    String category = record.isSet("category") ? record.get("category") : "General";
                    String mrpStr = record.isSet("declared_mrp") ? record.get("declared_mrp") : (record.isSet("mrp") ? record.get("mrp") : "0");
                    String priceStr = record.isSet("listing_price") ? record.get("listing_price") : mrpStr;
                    String origin = record.isSet("country_of_origin") ? record.get("country_of_origin") : "India";
                    String url = record.isSet("product_url") ? record.get("product_url") : "";

                    Double mrp = parseDoubleSafe(mrpStr, 0.0);
                    Double price = parseDoubleSafe(priceStr, mrp);

                    ProductCreateRequest req = new ProductCreateRequest();
                    req.setName(name);
                    req.setBrandName(brand);
                    req.setSellerName(seller);
                    req.setCategoryName(category);
                    req.setDeclaredMrp(mrp);
                    req.setListingPrice(price);
                    req.setCountryOfOrigin(origin);
                    req.setProductUrl(url);
                    req.setIngestionSource("CSV_UPLOAD");

                    ingestManual(req, null);
                    success++;
                } catch (Exception e) {
                    errors.add("Row " + total + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new BadRequestException("Failed to parse CSV file: " + e.getMessage());
        }

        return new BatchIngestResultDto(batchId, total, success, total - success, null, errors);
    }

    @Transactional
    public BatchIngestResultDto ingestJson(MultipartFile file) {
        String batchId = "BATCH-JSON-" + System.currentTimeMillis();
        List<String> errors = new ArrayList<>();
        int success = 0;
        int total = 0;

        try {
            List<Map<String, Object>> items = objectMapper.readValue(file.getInputStream(), new TypeReference<>() {});
            for (Map<String, Object> map : items) {
                total++;
                try {
                    ProductCreateRequest req = new ProductCreateRequest();
                    req.setName(String.valueOf(map.getOrDefault("name", "Product " + total)));
                    req.setBrandName((String) map.get("brand"));
                    req.setSellerName((String) map.get("seller"));
                    req.setCategoryName((String) map.get("category"));
                    req.setDeclaredMrp(parseDoubleSafe(String.valueOf(map.get("declared_mrp")), 0.0));
                    req.setListingPrice(parseDoubleSafe(String.valueOf(map.get("listing_price")), req.getDeclaredMrp()));
                    req.setCountryOfOrigin((String) map.getOrDefault("country_of_origin", "India"));
                    req.setProductUrl((String) map.get("product_url"));
                    req.setIngestionSource("JSON_UPLOAD");

                    ingestManual(req, (String) map.get("image_path"));
                    success++;
                } catch (Exception e) {
                    errors.add("Item " + total + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new BadRequestException("Failed to parse JSON file: " + e.getMessage());
        }

        return new BatchIngestResultDto(batchId, total, success, total - success, null, errors);
    }

    @Transactional
    public Product ingestUrl(String url) {
        ProductDataSource.IngestedProductData data = mockProductDataSource.fetchProductFromUrl(url);

        ProductCreateRequest req = new ProductCreateRequest();
        req.setName(data.name());
        req.setBrandName(data.brandName());
        req.setSellerName(data.sellerName());
        req.setCategoryName(data.categoryName());
        req.setDeclaredMrp(data.declaredMrp());
        req.setListingPrice(data.listingPrice());
        req.setDescription(data.description());
        req.setCountryOfOrigin(data.countryOfOrigin());
        req.setProductUrl(url);
        req.setIngestionSource("URL_CRAWL");
        req.setListingAttributes(data.listingAttributes());

        return ingestManual(req, data.imagePath());
    }

    @Transactional
    public BatchIngestResultDto ingestSimulatedCrawl(int count) {
        String batchId = "BATCH-CRAWL-" + System.currentTimeMillis();
        List<ProductDataSource.IngestedProductData> batch = mockProductDataSource.fetchSimulatedCrawlBatch(count);
        int success = 0;

        for (ProductDataSource.IngestedProductData data : batch) {
            ProductCreateRequest req = new ProductCreateRequest();
            req.setName(data.name());
            req.setBrandName(data.brandName());
            req.setSellerName(data.sellerName());
            req.setCategoryName(data.categoryName());
            req.setDeclaredMrp(data.declaredMrp());
            req.setListingPrice(data.listingPrice());
            req.setDescription(data.description());
            req.setCountryOfOrigin(data.countryOfOrigin());
            req.setProductUrl(data.productUrl());
            req.setIngestionSource("SIMULATED_CRAWL");
            req.setListingAttributes(data.listingAttributes());

            ingestManual(req, data.imagePath());
            success++;
        }

        return new BatchIngestResultDto(batchId, batch.size(), success, 0, null, Collections.emptyList());
    }

    private Long resolveBrandId(String brandName, Long explicitId) {
        if (explicitId != null) return explicitId;
        if (brandName == null || brandName.isBlank()) return 1L;
        return brandRepository.findByNameIgnoreCase(brandName)
            .map(Brand::getId)
            .orElseGet(() -> brandRepository.save(Brand.builder().name(brandName).build()).getId());
    }

    private Long resolveSellerId(String sellerName, Long explicitId) {
        if (explicitId != null) return explicitId;
        if (sellerName == null || sellerName.isBlank()) return 1L;
        return sellerRepository.findAll().stream()
            .filter(s -> s.getName().equalsIgnoreCase(sellerName))
            .findFirst()
            .map(Seller::getId)
            .orElseGet(() -> sellerRepository.save(Seller.builder().name(sellerName).location("India").build()).getId());
    }

    private Long resolveCategoryId(String categoryName, Long explicitId) {
        if (explicitId != null) return explicitId;
        if (categoryName == null || categoryName.isBlank()) return 1L;
        return categoryRepository.findByNameIgnoreCase(categoryName)
            .map(Category::getId)
            .orElse(1L);
    }

    private Double parseDoubleSafe(String val, Double defaultVal) {
        if (val == null || val.isBlank()) return defaultVal;
        try {
            return Double.parseDouble(val.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private String serializeMap(Map<String, String> map) {
        if (map == null) return "{}";
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }
}
