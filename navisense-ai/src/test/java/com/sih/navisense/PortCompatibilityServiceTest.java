package com.sih.navisense;

import com.sih.navisense.entity.Port;
import com.sih.navisense.entity.VesselType;
import com.sih.navisense.repository.PortRepository;
import com.sih.navisense.service.PortCompatibilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class PortCompatibilityServiceTest {

    private PortRepository portRepository;
    private PortCompatibilityService compatibilityService;

    @BeforeEach
    void setUp() {
        portRepository = Mockito.mock(PortRepository.class);
        compatibilityService = new PortCompatibilityService(portRepository);
    }

    @Test
    @DisplayName("Panamax should be compatible with Paradip port")
    void testPanamaxCompatibleWithParadip() {
        VesselType panamax = new VesselType(1L, "Panamax", 65000.0, 85000.0, 75000.0, 14.5, 229.0, 32.3, 14.0, 32.0, 1.0, 18500.0);
        Port paradip = new Port(1L, "Paradip", "India", "INPRT", 16.5, 260.0, 43.0, 3000.0, 55.0, 36.0, 20.26, 86.67, "DESTINATION");
        Port hayPoint = new Port(2L, "Hay Point", "Australia", "AUHAY", 19.5, 330.0, 55.0, 6000.0, 25.0, 24.0, -21.28, 149.30, "LOADING");

        when(portRepository.findByNameIgnoreCase("Paradip")).thenReturn(Optional.of(paradip));
        when(portRepository.findByNameIgnoreCase("Hay Point")).thenReturn(Optional.of(hayPoint));

        PortCompatibilityService.CompatibilityResult result = compatibilityService.checkCompatibility(panamax, "Hay Point", "Paradip");

        assertTrue(result.isCompatible(), "Panamax should be physically compatible with Paradip and Hay Point");
        assertTrue(result.getRejectionReasons().isEmpty());
    }

    @Test
    @DisplayName("Capesize should be rejected at Paradip due to draft restriction")
    void testCapesizeRejectedAtParadip() {
        VesselType capesize = new VesselType(2L, "Capesize", 120000.0, 200000.0, 175000.0, 18.2, 292.0, 45.0, 14.5, 52.0, 0.82, 26000.0);
        Port paradip = new Port(1L, "Paradip", "India", "INPRT", 16.5, 260.0, 43.0, 3000.0, 55.0, 36.0, 20.26, 86.67, "DESTINATION");
        Port hayPoint = new Port(2L, "Hay Point", "Australia", "AUHAY", 19.5, 330.0, 55.0, 6000.0, 25.0, 24.0, -21.28, 149.30, "LOADING");

        when(portRepository.findByNameIgnoreCase("Paradip")).thenReturn(Optional.of(paradip));
        when(portRepository.findByNameIgnoreCase("Hay Point")).thenReturn(Optional.of(hayPoint));

        PortCompatibilityService.CompatibilityResult result = compatibilityService.checkCompatibility(capesize, "Hay Point", "Paradip");

        assertFalse(result.isCompatible(), "Capesize should be rejected at Paradip");
        assertFalse(result.getRejectionReasons().isEmpty());
        assertTrue(result.getPrimaryReason().contains("draft restriction"), "Rejection reason must mention draft");
    }

    @Test
    @DisplayName("Capesize should be compatible with deepwater Gangavaram port")
    void testCapesizeCompatibleWithGangavaram() {
        VesselType capesize = new VesselType(2L, "Capesize", 120000.0, 200000.0, 175000.0, 18.2, 292.0, 45.0, 14.5, 52.0, 0.82, 26000.0);
        Port gangavaram = new Port(3L, "Gangavaram", "India", "INGGV", 19.5, 320.0, 50.0, 4000.0, 35.0, 24.0, 17.62, 83.24, "DESTINATION");
        Port hayPoint = new Port(2L, "Hay Point", "Australia", "AUHAY", 19.5, 330.0, 55.0, 6000.0, 25.0, 24.0, -21.28, 149.30, "LOADING");

        when(portRepository.findByNameIgnoreCase("Gangavaram")).thenReturn(Optional.of(gangavaram));
        when(portRepository.findByNameIgnoreCase("Hay Point")).thenReturn(Optional.of(hayPoint));

        PortCompatibilityService.CompatibilityResult result = compatibilityService.checkCompatibility(capesize, "Hay Point", "Gangavaram");

        assertTrue(result.isCompatible(), "Capesize should clear Gangavaram 19.5m draft");
    }
}
