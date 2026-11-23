package com.example.lmchecker.repository;

import com.example.lmchecker.entity.OCRResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OCRResultRepository extends JpaRepository<OCRResult, Long> {
    List<OCRResult> findByProductImageId(Long productImageId);
}
