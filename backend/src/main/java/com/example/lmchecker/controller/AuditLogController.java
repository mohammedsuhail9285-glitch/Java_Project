package com.example.lmchecker.controller;

import com.example.lmchecker.dto.ApiResponse;
import com.example.lmchecker.entity.AuditLog;
import com.example.lmchecker.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'REGULATOR', 'REVIEWER', 'ANALYST')")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLog> logs = auditLogRepository.findAllByOrderByTimestampDesc(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Audit logs retrieved", logs.getContent()));
    }

    @GetMapping("/entity/{entity}/{entityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGULATOR', 'REVIEWER', 'ANALYST')")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getByEntity(
            @PathVariable String entity,
            @PathVariable String entityId) {
        List<AuditLog> logs = auditLogRepository.findByEntityAndEntityIdOrderByTimestampDesc(entity, entityId);
        return ResponseEntity.ok(ApiResponse.ok("Entity audit logs retrieved", logs));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REGULATOR', 'REVIEWER', 'ANALYST')")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getByUser(@PathVariable Long userId) {
        List<AuditLog> logs = auditLogRepository.findByUserIdOrderByTimestampDesc(userId);
        return ResponseEntity.ok(ApiResponse.ok("User audit logs retrieved", logs));
    }
}
