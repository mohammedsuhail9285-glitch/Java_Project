package com.example.lmchecker.repository;

import com.example.lmchecker.entity.FreightForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreightForecastRepository extends JpaRepository<FreightForecast, Long> {
    List<FreightForecast> findByTradeRouteId(Long tradeRouteId);
    List<FreightForecast> findByVesselClassCode(String vesselClassCode);
}

