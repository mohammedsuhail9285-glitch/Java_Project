package com.example.lmchecker.controller;

import com.example.lmchecker.dto.ReviewRequest;
import com.example.lmchecker.dto.ViolationDto;
import com.example.lmchecker.entity.Severity;
import com.example.lmchecker.entity.ViolationStatus;
import com.example.lmchecker.security.UserPrincipal;
import com.example.lmchecker.service.ViolationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/violations")
public class ViolationController {

    private final ViolationService violationService;

    public ViolationController(ViolationService violationService) {
        this.violationService = violationService;
    }

    @GetMapping
    public ResponseEntity<Page<ViolationDto>> getViolations(
            @RequestParam(required = false) ViolationStatus status,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) String ruleId,
            @RequestParam(required = false) Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<ViolationDto> result = violationService.getViolations(status, severity, ruleId, productId, pageRequest);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ViolationDto> getViolationById(@PathVariable Long id) {
        return ResponseEntity.ok(violationService.getViolationById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ViolationDto>> getViolationsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(violationService.getViolationsByProduct(productId));
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('REGULATOR', 'REVIEWER', 'ADMIN')")
    public ResponseEntity<ViolationDto> processReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest servletRequest
    ) {
        Long reviewerId = principal != null ? principal.getId() : 1L;
        String username = principal != null ? principal.getUsername() : "system";
        String ip = servletRequest.getRemoteAddr();

        ViolationDto updated = violationService.processReview(id, request, reviewerId, username, ip);
        return ResponseEntity.ok(updated);
    }
}
