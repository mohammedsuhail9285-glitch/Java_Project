package com.example.lmchecker.repository;

import com.example.lmchecker.entity.Severity;
import com.example.lmchecker.entity.Violation;
import com.example.lmchecker.entity.ViolationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long> {

    Optional<Violation> findByViolationId(String violationId);
    List<Violation> findByProductId(Long productId);

    @Query("SELECT v FROM Violation v WHERE " +
           "(:status IS NULL OR v.status = :status) AND " +
           "(:severity IS NULL OR v.severity = :severity) AND " +
           "(:ruleId IS NULL OR v.ruleId = :ruleId) AND " +
           "(:productId IS NULL OR v.productId = :productId)")
    Page<Violation> searchViolations(
        @Param("status") ViolationStatus status,
        @Param("severity") Severity severity,
        @Param("ruleId") String ruleId,
        @Param("productId") Long productId,
        Pageable pageable
    );

    long countByStatus(ViolationStatus status);
    long countBySeverity(Severity severity);
    void deleteByProductId(Long productId);
}
