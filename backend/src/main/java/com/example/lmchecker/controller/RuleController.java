package com.example.lmchecker.controller;

import com.example.lmchecker.audit.AuditLogService;
import com.example.lmchecker.entity.Rule;
import com.example.lmchecker.exception.ResourceNotFoundException;
import com.example.lmchecker.repository.RuleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rules")
public class RuleController {

    private final RuleRepository ruleRepository;
    private final AuditLogService auditLogService;

    public RuleController(RuleRepository ruleRepository, AuditLogService auditLogService) {
        this.ruleRepository = ruleRepository;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<Rule>> getAllRules() {
        return ResponseEntity.ok(ruleRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rule> getRuleById(@PathVariable Long id) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found with id: " + id));
        return ResponseEntity.ok(rule);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGULATOR')")
    public ResponseEntity<Rule> updateRule(@PathVariable Long id, @RequestBody Rule updatedRule, HttpServletRequest request) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found with id: " + id));

        String oldVal = "Enabled=" + rule.getEnabled() + ", Severity=" + rule.getSeverity();

        if (updatedRule.getRuleName() != null) rule.setRuleName(updatedRule.getRuleName());
        if (updatedRule.getDescription() != null) rule.setDescription(updatedRule.getDescription());
        if (updatedRule.getCategory() != null) rule.setCategory(updatedRule.getCategory());
        if (updatedRule.getSeverity() != null) rule.setSeverity(updatedRule.getSeverity());
        if (updatedRule.getValidationType() != null) rule.setValidationType(updatedRule.getValidationType());
        if (updatedRule.getExpectedField() != null) rule.setExpectedField(updatedRule.getExpectedField());
        if (updatedRule.getEnabled() != null) rule.setEnabled(updatedRule.getEnabled());
        if (updatedRule.getSource() != null) rule.setSource(updatedRule.getSource());
        if (updatedRule.getRemediationMessage() != null) rule.setRemediationMessage(updatedRule.getRemediationMessage());
        rule.setVersion(rule.getVersion() + 1);
        rule.setUpdatedAt(LocalDateTime.now());

        Rule saved = ruleRepository.save(rule);

        auditLogService.logAction(
                "UPDATE_RULE",
                "RULE",
                String.valueOf(id),
                oldVal,
                "Enabled=" + saved.getEnabled() + ", Severity=" + saved.getSeverity() + ", Version=" + saved.getVersion(),
                request
        );

        return ResponseEntity.ok(saved);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Rule> createRule(@Valid @RequestBody Rule newRule, HttpServletRequest request) {
        newRule.setVersion(1);
        newRule.setCreatedAt(LocalDateTime.now());
        newRule.setUpdatedAt(LocalDateTime.now());
        Rule saved = ruleRepository.save(newRule);

        auditLogService.logAction(
                "CREATE_RULE",
                "RULE",
                String.valueOf(saved.getId()),
                null,
                "Created Rule " + saved.getRuleId() + " (" + saved.getRuleName() + ")",
                request
        );

        return ResponseEntity.ok(saved);
    }
}
