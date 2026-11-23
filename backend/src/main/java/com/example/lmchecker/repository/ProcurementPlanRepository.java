package com.example.lmchecker.repository;

import com.example.lmchecker.entity.ProcurementPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcurementPlanRepository extends JpaRepository<ProcurementPlan, Long> {
    Optional<ProcurementPlan> findByPlanReference(String planReference);
    List<ProcurementPlan> findByStatusOrderByCreatedAtDesc(String status);
    List<ProcurementPlan> findAllByOrderByCreatedAtDesc();
}

