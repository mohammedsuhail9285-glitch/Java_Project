package com.example.lmchecker.service;

import com.example.lmchecker.dto.ProductCreateRequest;
import com.example.lmchecker.dto.ProductDetailDto;
import com.example.lmchecker.entity.*;
import com.example.lmchecker.exception.ResourceNotFoundException;
import com.example.lmchecker.ingestion.BatchIngestionService;
import com.example.lmchecker.repository.*;
import com.example.lmchecker.storage.FileStorageService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductListingRepository productListingRepository;
    private final ProductImageRepository productImageRepository;
    private final ExtractedFieldRepository extractedFieldRepository;
    private final ComplianceCheckRepository complianceCheckRepository;
    private final ViolationRepository violationRepository;
    private final EvidenceRepository evidenceRepository;
    private final RuleRepository ruleRepository;
    private final BrandRepository brandRepository;
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final BatchIngestionService batchIngestionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductService(
            ProductRepository productRepository,
            ProductListingRepository productListingRepository,
            ProductImageRepository productImageRepository,
            ExtractedFieldRepository extractedFieldRepository,
            ComplianceCheckRepository complianceCheckRepository,
            ViolationRepository violationRepository,
            EvidenceRepository evidenceRepository,
            RuleRepository ruleRepository,
            BrandRepository brandRepository,
            SellerRepository sellerRepository,
            CategoryRepository categoryRepository,
            FileStorageService fileStorageService,
            BatchIngestionService batchIngestionService) {
        this.productRepository = productRepository;
        this.productListingRepository = productListingRepository;
        this.productImageRepository = productImageRepository;
        this.extractedFieldRepository = extractedFieldRepository;
        this.complianceCheckRepository = complianceCheckRepository;
        this.violationRepository = violationRepository;
        this.evidenceRepository = evidenceRepository;
        this.ruleRepository = ruleRepository;
        this.brandRepository = brandRepository;
        this.sellerRepository = sellerRepository;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
        this.batchIngestionService = batchIngestionService;
    }

    public Page<ProductDetailDto> searchProducts(String keyword, Long categoryId, Long sellerId, Long brandId, String status, Pageable pageable) {
        Page<Product> page = productRepository.searchProducts(keyword, categoryId, sellerId, brandId, status, pageable);
        return page.map(this::mapToDetailDto);
    }

    public ProductDetailDto getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToDetailDto(product);
    }

    @Transactional
    public ProductDetailDto createProductManual(ProductCreateRequest request, MultipartFile image) {
        String storedPath = null;
        if (image != null && !image.isEmpty()) {
            storedPath = fileStorageService.storeOriginal(image);
        }
        Product created = batchIngestionService.ingestManual(request, storedPath);
        return getProductById(created.getId());
    }

    private ProductDetailDto mapToDetailDto(Product p) {
        ProductDetailDto dto = new ProductDetailDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setBrandId(p.getBrandId());
        dto.setSellerId(p.getSellerId());
        dto.setCategoryId(p.getCategoryId());
        dto.setDeclaredMrp(p.getDeclaredMrp());
        dto.setListingPrice(p.getListingPrice());
        dto.setDescription(p.getDescription());
        dto.setCountryOfOrigin(p.getCountryOfOrigin());
        dto.setProductUrl(p.getProductUrl());
        dto.setIngestionSource(p.getIngestionSource());
        dto.setStatus(p.getStatus());
        dto.setCreatedAt(p.getCreatedAt());

        // Brand, Seller, Category names
        if (p.getBrandId() != null) {
            brandRepository.findById(p.getBrandId()).ifPresent(b -> dto.setBrandName(b.getName()));
        }
        if (p.getSellerId() != null) {
            sellerRepository.findById(p.getSellerId()).ifPresent(s -> dto.setSellerName(s.getName()));
        }
        if (p.getCategoryId() != null) {
            categoryRepository.findById(p.getCategoryId()).ifPresent(c -> dto.setCategoryName(c.getName()));
        }

        // Images
        List<ProductImage> images = productImageRepository.findByProductId(p.getId());
        dto.setImageUrls(images.stream().map(ProductImage::getOriginalPath).collect(Collectors.toList()));

        // Listing fields
        productListingRepository.findFirstByProductIdOrderByScrapedAtDesc(p.getId()).ifPresent(listing -> {
            try {
                if (listing.getRawExtractedFields() != null) {
                    Map<String, Object> map = objectMapper.readValue(listing.getRawExtractedFields(), new TypeReference<>() {});
                    dto.setListingFields(map);
                }
            } catch (Exception ignored) {}
        });

        // Extracted fields
        List<ExtractedField> fields = extractedFieldRepository.findByProductId(p.getId());
        dto.setExtractedFields(fields.stream().map(f -> new ProductDetailDto.ExtractedFieldDto(
            f.getId(),
            f.getFieldType().name(),
            f.getRawValue(),
            f.getNormalizedValue(),
            f.getUnit(),
            f.getSource().name(),
            f.getConfidence(),
            f.getBoundingBox()
        )).collect(Collectors.toList()));

        // Compliance check summary
        complianceCheckRepository.findFirstByProductIdOrderByCreatedAtDesc(p.getId()).ifPresent(c -> {
            dto.setComplianceCheck(new ProductDetailDto.ComplianceCheckSummaryDto(
                c.getOverallScore(),
                c.getMandatoryFieldCompletionPct(),
                c.getCriticalCount(),
                c.getMajorCount(),
                c.getMinorCount(),
                c.getOcrConfidence(),
                c.getAiConfidence(),
                c.getRuleEngineConfidence(),
                c.getRiskLevel(),
                c.getSummaryNotes()
            ));
        });

        // Violations
        List<Violation> violations = violationRepository.findByProductId(p.getId());
        dto.setViolations(violations.stream().map(v -> {
            ProductDetailDto.ViolationDto vDto = new ProductDetailDto.ViolationDto();
            vDto.setId(v.getId());
            vDto.setViolationId(v.getViolationId());
            vDto.setRuleId(v.getRuleId());
            vDto.setSeverity(v.getSeverity().name());
            vDto.setDetectedValue(v.getDetectedValue());
            vDto.setExpectedCondition(v.getExpectedCondition());
            vDto.setStatus(v.getStatus().name());
            vDto.setOcrConfidence(v.getOcrConfidence());
            vDto.setDetectionConfidence(v.getDetectionConfidence());
            vDto.setReviewerId(v.getReviewerId());
            vDto.setReviewerComment(v.getReviewerComment());

            ruleRepository.findByRuleId(v.getRuleId()).ifPresent(r -> {
                vDto.setRuleName(r.getRuleName());
                vDto.setLegalSource(r.getSource());
                vDto.setRemediationMessage(r.getRemediationMessage());
            });

            if (v.getEvidenceId() != null) {
                evidenceRepository.findById(v.getEvidenceId()).ifPresent(ev -> {
                    vDto.setOriginalImagePath(ev.getOriginalImagePath());
                    vDto.setCroppedImagePath(ev.getCroppedImagePath());
                    vDto.setOcrText(ev.getOcrText());
                    vDto.setBoundingBox(ev.getBoundingBox());
                });
            }
            return vDto;
        }).collect(Collectors.toList()));

        return dto;
    }
}
