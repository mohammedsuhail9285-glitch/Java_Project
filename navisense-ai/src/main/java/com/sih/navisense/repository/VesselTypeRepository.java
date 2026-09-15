package com.sih.navisense.repository;

import com.sih.navisense.entity.VesselType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VesselTypeRepository extends JpaRepository<VesselType, Long> {
    Optional<VesselType> findByNameIgnoreCase(String name);
}
