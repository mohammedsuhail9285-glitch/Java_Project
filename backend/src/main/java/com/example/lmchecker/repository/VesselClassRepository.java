package com.example.lmchecker.repository;

import com.example.lmchecker.entity.VesselClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VesselClassRepository extends JpaRepository<VesselClass, Long> {
    Optional<VesselClass> findByClassCode(String classCode);
}

