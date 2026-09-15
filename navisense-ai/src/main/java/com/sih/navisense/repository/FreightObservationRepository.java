package com.sih.navisense.repository;

import com.sih.navisense.entity.FreightObservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FreightObservationRepository extends JpaRepository<FreightObservation, Long> {

    List<FreightObservation> findByOriginCountryIgnoreCaseAndDestinationPortIgnoreCaseAndVesselClassIgnoreCaseOrderByObservationDateAsc(
            String originCountry, String destinationPort, String vesselClass);

    List<FreightObservation> findByOriginCountryIgnoreCaseAndDestinationPortIgnoreCaseOrderByObservationDateAsc(
            String originCountry, String destinationPort);

    List<FreightObservation> findByVesselClassIgnoreCaseOrderByObservationDateAsc(String vesselClass);

    @Query("SELECT f FROM FreightObservation f WHERE LOWER(f.originCountry) = LOWER(:origin) AND LOWER(f.destinationPort) = LOWER(:dest) AND LOWER(f.vesselClass) = LOWER(:vessel) ORDER BY f.observationDate DESC")
    List<FreightObservation> findRecentObservations(
            @Param("origin") String origin,
            @Param("dest") String dest,
            @Param("vessel") String vessel,
            Pageable pageable);

    @Query("SELECT DISTINCT f.originCountry FROM FreightObservation f")
    List<String> findDistinctOrigins();

    @Query("SELECT DISTINCT f.destinationPort FROM FreightObservation f")
    List<String> findDistinctDestinations();

    @Query("SELECT DISTINCT f.vesselClass FROM FreightObservation f")
    List<String> findDistinctVesselClasses();

    long countByObservationDateBetween(LocalDate start, LocalDate end);
}
