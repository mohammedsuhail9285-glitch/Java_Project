package com.sih.navisense.service;

import com.sih.navisense.dto.PortAlternativeDto;
import com.sih.navisense.entity.Port;
import com.sih.navisense.entity.VesselType;
import com.sih.navisense.provider.CongestionDataProvider;
import com.sih.navisense.repository.PortRepository;
import com.sih.navisense.util.CurrencyFormatter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class IdleTimeOptimizationService {

    private final PortRepository portRepository;
    private final CongestionDataProvider congestionDataProvider;
    private final PortCompatibilityService compatibilityService;

    public IdleTimeOptimizationService(
            PortRepository portRepository,
            CongestionDataProvider congestionDataProvider,
            PortCompatibilityService compatibilityService) {
        this.portRepository = portRepository;
        this.congestionDataProvider = congestionDataProvider;
        this.compatibilityService = compatibilityService;
    }

    public List<PortAlternativeDto> evaluatePortAlternatives(
            String selectedDestinationPort,
            VesselType selectedVessel,
            BigDecimal currentCostInr) {

        List<PortAlternativeDto> alternatives = new ArrayList<>();
        List<Port> destinationPorts = portRepository.findByPortTypeIgnoreCase("DESTINATION");

        for (Port port : destinationPorts) {
            if (port.getName().equalsIgnoreCase(selectedDestinationPort)) {
                continue; // Skip the currently selected port
            }

            double congestion = congestionDataProvider.getCongestionIndex(port.getName());
            boolean accommodates = (selectedVessel == null) ||
                    (selectedVessel.getDraft() <= port.getMaximumDraft() &&
                     selectedVessel.getLoa() <= port.getMaximumLOA() &&
                     selectedVessel.getBeam() <= port.getMaximumBeam());

            // Estimated relative cost modifier based on port draft & congestion
            // Deeper ports with lower congestion yield 2-6% operational savings
            double modFactor = 1.0;
            if (congestion < 40.0 && accommodates) {
                modFactor = 0.96; // 4% operational & demurrage saving
            } else if (congestion > 60.0 || !accommodates) {
                modFactor = 1.05; // 5% costlier / feeder required
            }

            BigDecimal estCost = (currentCostInr != null && currentCostInr.compareTo(BigDecimal.ZERO) > 0)
                    ? currentCostInr.multiply(BigDecimal.valueOf(modFactor)).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            String verdict;
            String deltaDesc;
            if (modFactor < 0.99 && accommodates) {
                verdict = "BETTER";
                BigDecimal diff = currentCostInr != null ? currentCostInr.subtract(estCost) : BigDecimal.ZERO;
                deltaDesc = "Lower turnaround queue saves ~" + CurrencyFormatter.formatInrCroresOrLakhs(diff) + " with full berth capacity";
            } else if (!accommodates) {
                verdict = "INCOMPATIBLE";
                deltaDesc = "Draft/LOA constraint: Cannot berth " + (selectedVessel != null ? selectedVessel.getName() : "vessel");
            } else {
                verdict = "COMPARABLE";
                deltaDesc = "Similar operational timeline and port handling profile";
            }

            alternatives.add(new PortAlternativeDto(
                    port.getName(),
                    congestion,
                    port.getMaximumDraft(),
                    accommodates,
                    estCost,
                    CurrencyFormatter.formatInrCroresOrLakhs(estCost),
                    deltaDesc,
                    verdict
            ));
        }

        // Sort: BETTER first, then COMPARABLE, then INCOMPATIBLE
        alternatives.sort((a, b) -> {
            int scoreA = "BETTER".equals(a.getComparisonVerdict()) ? 3 : ("COMPARABLE".equals(a.getComparisonVerdict()) ? 2 : 1);
            int scoreB = "BETTER".equals(b.getComparisonVerdict()) ? 3 : ("COMPARABLE".equals(b.getComparisonVerdict()) ? 2 : 1);
            return Integer.compare(scoreB, scoreA);
        });

        return alternatives;
    }
}
