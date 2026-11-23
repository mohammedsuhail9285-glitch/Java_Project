package com.example.lmchecker.repository;

import com.example.lmchecker.entity.ScanResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {
    Optional<ScanResult> findByScanJobId(Long scanJobId);
}
