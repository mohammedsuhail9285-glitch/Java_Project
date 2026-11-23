package com.example.lmchecker.compliance;

import com.example.lmchecker.entity.*;
import com.example.lmchecker.repository.ComplianceCheckRepository;
import com.example.lmchecker.repository.EvidenceRepository;
import com.example.lmchecker.repository.ProductRepository;
import com.example.lmchecker.repository.RuleRepository;
import com.example.lmchecker.repository.ViolationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RuleExecutionEngine {

    private static final Logger log = LoggerFactory.getLogger(RuleExecutionEngine.class);

    private final RuleRepository ruleRepository;
    private final RuleRegistry ruleRegistry;
    private final FieldPresenceEvaluator fieldPresenceEvaluator;
    private final ComplianceScorer complianceScorer;
    private final ComplianceCheckRepository complianceCheckRepository;
    private final ViolationRepository violationRepository;
    private final EvidenceRepository evidenceRepository;
    private final ProductRepository productRepository;

    public RuleExecutionEngine(RuleRepository ruleRepository,
                               RuleRegistry ruleRegistry,
                               FieldPresenceEvaluator fieldPresenceEvaluator,
                               ComplianceScorer complianceScorer,
                               ComplianceCheckRepository complianceCheckRepository,
                               ViolationRepository violationRepository,
                               EvidenceRepository evidenceRepository,
                               ProductRepository productRepository) {
        this.ruleRepository = ruleRepository;
        this.ruleRegistry = ruleRegistry;
        this.fieldPresenceEvaluator = fieldPresenceEvaluator;
        this.complianceScorer = complianceScorer;
        this.complianceCheckRepository = complianceCheckRepository;
        this.violationRepository = violationRepository;
        this.evidenceRepository = evidenceRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public ComplianceCheck evaluateProduct(ProductComplianceContext context, Long scanJobId) {
        Product product = context.getProduct();
        List<Rule> activeRules = ruleRepository.findByEnabledTrue();

        List<RuleResult> results = new ArrayList<>();

        for (Rule rule : activeRules) {
            RuleEvaluator evaluator = ruleRegistry.getEvaluator(rule.getValidationType());
            if (evaluator == null) {
                evaluator = fieldPresenceEvaluator;
            }

            try {
                RuleResult res = evaluator.evaluate(rule, context);
                if (res != null) {
                    results.add(res);
                }
            } catch (Exception e) {
                log.error("Error evaluating rule {} on product {}: {}", rule.getRuleId(), product.getId(), e.getMessage());
            }
        }

        ComplianceScorer.ScoreComputation score = complianceScorer.computeScore(results, context);

        double ocrConfidence = context.getOcrResult() != null ? context.getOcrResult().getConfidence() : 90.0;
        double aiConfidence = 92.0;
        double ruleEngineConfidence = 95.0;

        ComplianceCheck check = ComplianceCheck.builder()
                .productId(product.getId())
                .scanJobId(scanJobId)
                .overallScore(score.getOverallScore())
                .mandatoryFieldCompletionPct(score.getMandatoryFieldCompletionPct())
                .criticalCount(score.getCriticalCount())
                .majorCount(score.getMajorCount())
                .minorCount(score.getMinorCount())
                .ocrConfidence(ocrConfidence)
                .aiConfidence(aiConfidence)
                .ruleEngineConfidence(ruleEngineConfidence)
                .riskLevel(score.getRiskLevel())
                .summaryNotes(score.getSummaryNotes())
                .createdAt(LocalDateTime.now())
                .build();

        check = complianceCheckRepository.save(check);

        // Process Violations and Evidences
        for (RuleResult r : results) {
            if (!r.isCompliant()) {
                String originalPath = context.getImage() != null ? context.getImage().getOriginalPath() : null;
                String sourceUrl = context.getProduct() != null ? context.getProduct().getProductUrl() : null;

                Evidence evidence = Evidence.builder()
                        .originalImagePath(originalPath)
                        .croppedImagePath(originalPath) // cropped region or original
                        .ocrText(context.getRawOCRText())
                        .sourceUrlOrDataset(sourceUrl)
                        .boundingBox(r.getBoundingBox())
                        .detectionTimestamp(LocalDateTime.now())
                        .build();

                evidence = evidenceRepository.save(evidence);

                String vId = "VIO-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

                Violation violation = Violation.builder()
                        .violationId(vId)
                        .productId(product.getId())
                        .ruleId(r.getRuleId())
                        .severity(r.getSeverity())
                        .detectedValue(r.getDetectedValue())
                        .expectedCondition(r.getExpectedCondition())
                        .evidenceId(evidence.getId())
                        .source(r.getSource() != null ? r.getSource() : FieldSource.IMAGE)
                        .ocrConfidence(ocrConfidence)
                        .detectionConfidence(r.getConfidence() > 0 ? r.getConfidence() : 90.0)
                        .timestamp(LocalDateTime.now())
                        .status(ViolationStatus.NEW)
                        .build();

                violation = violationRepository.save(violation);

                evidence.setViolationId(violation.getId());
                evidenceRepository.save(evidence);
            }
        }

        // Update Product Status adhering strictly to non-determination regulatory copy
        if (score.getCriticalCount() > 0 || score.getMajorCount() > 0 || score.getOverallScore() < 80.0) {
            product.setStatus("POTENTIAL_NON_COMPLIANT");
        } else if (score.getMinorCount() > 0 || score.getOverallScore() < 95.0) {
            product.setStatus("REQUIRES_REVIEW");
        } else {
            product.setStatus("PASSED_AUTOMATED_CHECKS");
        }

        productRepository.save(product);

        return check;
    }
}
