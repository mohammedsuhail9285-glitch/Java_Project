package com.example.lmchecker.repository;

import com.example.lmchecker.entity.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    Optional<Evidence> findByViolationId(Long violationId);
}
