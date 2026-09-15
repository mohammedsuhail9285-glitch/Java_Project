package com.sih.navisense.service;

import com.sih.navisense.entity.Port;
import com.sih.navisense.entity.VesselType;
import com.sih.navisense.repository.PortRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PortCompatibilityService {

    private final PortRepository portRepository;

    public PortCompatibilityService(PortRepository portRepository) {
        this.portRepository = portRepository;
    }

    public static class CompatibilityResult {
        private final boolean compatible;
        private final List<String> rejectionReasons;

        public CompatibilityResult(boolean compatible, List<String> rejectionReasons) {
            this.compatible = compatible;
            this.rejectionReasons = rejectionReasons != null ? rejectionReasons : new ArrayList<>();
        }

        public boolean isCompatible() { return compatible; }
        public List<String> getRejectionReasons() { return rejectionReasons; }
        public String getPrimaryReason() {
            return rejectionReasons.isEmpty() ? "Passed physical port constraints" : rejectionReasons.get(0);
        }
    }

    public CompatibilityResult checkCompatibility(VesselType vessel, String loadingPortName, String destinationPortName) {
        if (vessel == null) {
            return new CompatibilityResult(false, List.of("Invalid or unspecified vessel type"));
        }

        List<String> reasons = new ArrayList<>();

        // Check Loading Port
        if (loadingPortName != null && !loadingPortName.isBlank()) {
            Optional<Port> loadPortOpt = portRepository.findByNameIgnoreCase(loadingPortName.trim());
            if (loadPortOpt.isPresent()) {
                checkPortConstraints(vessel, loadPortOpt.get(), "Loading Port (" + loadPortOpt.get().getName() + ")", reasons);
            }
        }

        // Check Destination Port
        if (destinationPortName != null && !destinationPortName.isBlank()) {
            Optional<Port> destPortOpt = portRepository.findByNameIgnoreCase(destinationPortName.trim());
            if (destPortOpt.isPresent()) {
                checkPortConstraints(vessel, destPortOpt.get(), "Destination Port (" + destPortOpt.get().getName() + ")", reasons);
            }
        }

        return new CompatibilityResult(reasons.isEmpty(), reasons);
    }

    public CompatibilityResult checkSinglePortCompatibility(VesselType vessel, Port port, String portRole) {
        List<String> reasons = new ArrayList<>();
        checkPortConstraints(vessel, port, portRole + " (" + port.getName() + ")", reasons);
        return new CompatibilityResult(reasons.isEmpty(), reasons);
    }

    private void checkPortConstraints(VesselType vessel, Port port, String portLabel, List<String> reasons) {
        // Draft check
        if (vessel.getDraft() > port.getMaximumDraft()) {
            reasons.add(String.format("%s draft restriction: Vessel requires %.1f m, but port allows max %.1f m",
                    portLabel, vessel.getDraft(), port.getMaximumDraft()));
        }

        // LOA check
        if (vessel.getLoa() > port.getMaximumLOA()) {
            reasons.add(String.format("%s LOA restriction: Vessel length is %.1f m, but max allowable is %.1f m",
                    portLabel, vessel.getLoa(), port.getMaximumLOA()));
        }

        // Beam check
        if (vessel.getBeam() > port.getMaximumBeam()) {
            reasons.add(String.format("%s beam restriction: Vessel beam is %.1f m, but max allowable is %.1f m",
                    portLabel, vessel.getBeam(), port.getMaximumBeam()));
        }
    }
}
