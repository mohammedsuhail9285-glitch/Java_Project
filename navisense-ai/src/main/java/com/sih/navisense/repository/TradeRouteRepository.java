package com.sih.navisense.repository;

import com.sih.navisense.entity.TradeRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRouteRepository extends JpaRepository<TradeRoute, Long> {
    Optional<TradeRoute> findByOriginCountryIgnoreCaseAndLoadingPortIgnoreCaseAndDestinationPortIgnoreCase(
            String originCountry, String loadingPort, String destinationPort);

    List<TradeRoute> findByOriginCountryIgnoreCase(String originCountry);

    List<TradeRoute> findByDestinationPortIgnoreCase(String destinationPort);

    Optional<TradeRoute> findByLoadingPortIgnoreCaseAndDestinationPortIgnoreCase(String loadingPort, String destinationPort);
}
