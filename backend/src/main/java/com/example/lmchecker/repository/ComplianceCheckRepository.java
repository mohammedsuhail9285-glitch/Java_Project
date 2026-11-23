package com.example.lmchecker.repository;

import com.example.lmchecker.entity.ComplianceCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComplianceCheckRepository extends JpaRepository<ComplianceCheck, Long> {
    Optional<ComplianceCheck> findFirstByProductIdOrderByCreatedAtDesc(Long productId);
}
