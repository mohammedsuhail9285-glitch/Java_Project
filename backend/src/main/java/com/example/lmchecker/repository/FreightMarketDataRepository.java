package com.example.lmchecker.repository;

import com.example.lmchecker.entity.FreightMarketData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FreightMarketDataRepository extends JpaRepository<FreightMarketData, Long> {
    Optional<FreightMarketData> findTopByOrderByRecordDateDesc();
    List<FreightMarketData> findTop30ByOrderByRecordDateDesc();
    Optional<FreightMarketData> findByRecordDate(LocalDate recordDate);
}

