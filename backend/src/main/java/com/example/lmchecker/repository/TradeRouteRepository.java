package com.example.lmchecker.repository;

import com.example.lmchecker.entity.TradeRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRouteRepository extends JpaRepository<TradeRoute, Long> {
    Optional<TradeRoute> findByRouteCode(String routeCode);
    List<TradeRoute> findByDestinationPortId(Long destinationPortId);
    List<TradeRoute> findByCommodity(String commodity);
}

