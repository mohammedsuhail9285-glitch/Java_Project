package com.example.lmchecker.service;

import com.example.lmchecker.audit.AuditLogService;
import com.example.lmchecker.dto.ReviewRequest;
import com.example.lmchecker.dto.ViolationDto;
import com.example.lmchecker.entity.*;
import com.example.lmchecker.exception.ResourceNotFoundException;
import com.example.lmchecker.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ViolationService {

    private final ViolationRepository violationRepository;
    private final EvidenceRepository evidenceRepository;
    private final ProductRepository productRepository;
    private final RuleRepository ruleRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public ViolationService(ViolationRepository violationRepository,
                            EvidenceRepository evidenceRepository,
                            ProductRepository productRepository,
                            RuleRepository ruleRepository,
                            ReviewRepository reviewRepository,
                            UserRepository userRepository,
                            AuditLogService auditLogService) {
        this.violationRepository = violationRepository;
        this.evidenceRepository = evidenceRepository;
        this.productRepository = productRepository;
        this.ruleRepository = ruleRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public Page<ViolationDto> getViolations(ViolationStatus status, Severity severity, String ruleId, Long productId, Pageable pageable) {
        Page<Violation> page = violationRepository.searchViolations(status, severity, ruleId, productId, pageable);
        return page.map(this::enrichViolationDto);
    }

    @Transactional(readOnly = true)
    public List<ViolationDto> getViolationsByProduct(Long productId) {
        return violationRepository.findByProductId(productId)
                .stream()
                .map(this::enrichViolationDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ViolationDto getViolationById(Long id) {
        Violation v = violationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Violation not found with id: " + id));
        return enrichViolationDto(v);
    }

    @Transactional
    public ViolationDto processReview(Long violationId, ReviewRequest request, Long reviewerId, String username, String ipAddress) {
        Violation v = violationRepository.findById(violationId)
                .orElseThrow(() -> new ResourceNotFoundException("Violation not found with id: " + violationId));

        String act = request.getAction().toUpperCase();
        ViolationStatus newStatus;

        switch (act) {
            case "CONFIRM":
                newStatus = ViolationStatus.CONFIRMED;
                break;
            case "DISMISS":
                newStatus = ViolationStatus.DISMISSED;
                break;
            case "RESOLVE":
                newStatus = ViolationStatus.RESOLVED;
                break;
            case "REQUEST_INFO":
            default:
                newStatus = ViolationStatus.UNDER_REVIEW;
                break;
        }

        v.setStatus(newStatus);
        v.setReviewerId(reviewerId);
        v.setReviewerComment(request.getComment());
        v.setResolution(request.getRemediationAction() != null ? request.getRemediationAction() : "Action: " + act);
        v.setResolutionTimestamp(LocalDateTime.now());

        Violation saved = violationRepository.save(v);

        // Record Review entry
        Review review = Review.builder()
                .violationId(v.getId())
                .reviewerId(reviewerId)
                .action(act)
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();
        reviewRepository.save(review);

        // Audit Log
        auditLogService.logAction(
                username,
                "REVIEW_VIOLATION",
                "VIOLATION",
                v.getId(),
                "Status updated to " + newStatus.name() + ". Rationale: " + request.getComment(),
                ipAddress
        );

        return enrichViolationDto(saved);
    }

    private ViolationDto enrichViolationDto(Violation v) {
        ViolationDto dto = new ViolationDto();
        dto.setId(v.getId());
        dto.setViolationId(v.getViolationId());
        dto.setProductId(v.getProductId());
        dto.setRuleId(v.getRuleId());
        dto.setSeverity(v.getSeverity());
        dto.setDetectedValue(v.getDetectedValue());
        dto.setExpectedCondition(v.getExpectedCondition());
        dto.setStatus(v.getStatus());
        dto.setSource(v.getSource());
        dto.setOcrConfidence(v.getOcrConfidence());
        dto.setDetectionConfidence(v.getDetectionConfidence());
        dto.setTimestamp(v.getTimestamp());

        // Product Name
        productRepository.findById(v.getProductId()).ifPresent(p -> dto.setProductName(p.getName()));

        // Rule Name
        ruleRepository.findByRuleId(v.getRuleId()).ifPresent(r -> dto.setRuleName(r.getRuleName()));

        // Evidence details
        if (v.getEvidenceId() != null) {
            evidenceRepository.findById(v.getEvidenceId()).ifPresent(e -> {
                dto.setEvidenceId(e.getId());
                dto.setOriginalImagePath(e.getOriginalImagePath());
                dto.setCroppedImagePath(e.getCroppedImagePath());
                dto.setOcrText(e.getOcrText());
                dto.setBoundingBox(e.getBoundingBox());
                dto.setSourceUrl(e.getSourceUrlOrDataset());
            });
        }

        // Reviewer details
        if (v.getReviewerId() != null) {
            dto.setReviewerId(v.getReviewerId());
            userRepository.findById(v.getReviewerId()).ifPresent(u -> dto.setReviewerUsername(u.getUsername()));
            dto.setReviewerComment(v.getReviewerComment());
            dto.setResolution(v.getResolution());
            dto.setResolutionTimestamp(v.getResolutionTimestamp());
        }

        return dto;
    }
}
