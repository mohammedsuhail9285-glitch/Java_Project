package com.example.lmchecker.audit;

import com.example.lmchecker.entity.AuditLog;
import com.example.lmchecker.repository.AuditLogRepository;
import com.example.lmchecker.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(String username, String action, String entity, Long entityId, String notes, String ipAddress) {

        try {
            AuditLog auditLog = AuditLog.builder()
                .userId(null)
                .username(username != null ? username : "SYSTEM")
                .action(action)
                .entity(entity)
                .entityId(entityId != null ? String.valueOf(entityId) : null)
                .ipAddress(ipAddress != null ? ipAddress : "127.0.0.1")
                .oldValue(null)
                .newValue(notes)
                .timestamp(LocalDateTime.now())
                .build();
            auditLogRepository.save(auditLog);
            log.info("AUDIT: User '{}' performed '{}' on entity '{}:{}'", username, action, entity, entityId);
        } catch (Exception e) {
            log.error("Failed to persist audit log entry: {}", e.getMessage(), e);
        }
    }

    public void logAction(String action, String entity, String entityId, String oldValue, String newValue, HttpServletRequest request) {

        try {
            Long userId = null;
            String username = "ANONYMOUS";

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
                userId = principal.getId();
                username = principal.getUsername();
            }

            String ipAddress = request != null ? getClientIp(request) : "127.0.0.1";

            AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .username(username)
                .action(action)
                .entity(entity)
                .entityId(entityId)
                .ipAddress(ipAddress)
                .oldValue(oldValue)
                .newValue(newValue)
                .timestamp(LocalDateTime.now())
                .build();

            auditLogRepository.save(auditLog);
            log.info("AUDIT: User '{}' performed '{}' on entity '{}:{}'", username, action, entity, entityId);
        } catch (Exception e) {
            log.error("Failed to persist audit log entry: {}", e.getMessage(), e);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwarded = request.getHeader("X-Forwarded-For");
        if (xForwarded != null && !xForwarded.isBlank()) {
            return xForwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
