package com.example.lmchecker.repository;

import com.example.lmchecker.entity.ScanJob;
import com.example.lmchecker.entity.ScanJobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanJobRepository extends JpaRepository<ScanJob, Long> {
    List<ScanJob> findByStatus(ScanJobStatus status);
    List<ScanJob> findByProductIdOrderByCreatedAtDesc(Long productId);
    Page<ScanJob> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
