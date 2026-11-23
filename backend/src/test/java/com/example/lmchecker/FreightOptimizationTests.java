package com.example.lmchecker;

import com.example.lmchecker.dto.CharterOptimizationRequestDto;
import com.example.lmchecker.dto.CharterOptimizationResponseDto;
import com.example.lmchecker.dto.FreightOverviewDto;
import com.example.lmchecker.entity.MaritimePort;
import com.example.lmchecker.entity.ProcurementPlan;
import com.example.lmchecker.entity.VesselClass;
import com.example.lmchecker.repository.MaritimePortRepository;
import com.example.lmchecker.repository.ProcurementPlanRepository;
import com.example.lmchecker.repository.VesselClassRepository;
import com.example.lmchecker.service.FreightReportService;
import com.example.lmchecker.service.FreightService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class FreightOptimizationTests {

    @Autowired
    private FreightService freightService;

    @Autowired
    private FreightReportService freightReportService;

    @Autowired
    private MaritimePortRepository portRepository;

    @Autowired
    private VesselClassRepository vesselClassRepository;

    @Autowired
    private ProcurementPlanRepository procurementPlanRepository;

    @Test
    @DisplayName("SIH26006: Port Draft Constraints - Haldia restricts Capesize/Panamax, Paradip allows Panamax")
    void testPortDraftConstraints() {
        System.out.println(">> [TEST 1/5] RUNNING: testPortDraftConstraints");
        MaritimePort haldia = portRepository.findByPortCode("INHLD").orElse(null);
        assertNotNull(haldia, "Haldia port should be seeded");
        assertEquals(11.0, haldia.getMaxDraftMeters());

        MaritimePort paradip = portRepository.findByPortCode("INPRT").orElse(null);
        assertNotNull(paradip, "Paradip port should be seeded");
        assertEquals(14.5, paradip.getMaxDraftMeters());

        VesselClass capesize = vesselClassRepository.findByClassCode("CAPESIZE").orElse(null);
        assertNotNull(capesize);
        assertTrue(capesize.getMaxDraftMeters() > haldia.getMaxDraftMeters(), "Capesize draft exceeds Haldia limit");

        VesselClass panamax = vesselClassRepository.findByClassCode("PANAMAX").orElse(null);
        assertNotNull(panamax);
        assertTrue(panamax.getMaxDraftMeters() <= paradip.getMaxDraftMeters(), "Panamax fits within Paradip draft limit");
        System.out.println(">> [TEST 1/5] PASSED: testPortDraftConstraints");
    }

    @Test
    @DisplayName("SIH26006: Charter Optimization - Haldia Coal Procurement selects Handysize and flags draft limits")
    void testHaldiaProcurementOptimization() {
        System.out.println(">> [TEST 2/5] RUNNING: testHaldiaProcurementOptimization");
        CharterOptimizationRequestDto req = new CharterOptimizationRequestDto();
        req.setCommodity("Indonesian Thermal Coal");
        req.setQuantityTons(42000.0);
        req.setOriginCountry("Indonesia");
        req.setDestinationPortCode("INHLD");
        req.setFobPriceUsd(95.0);
        req.setTargetLaycanStart(LocalDate.now().plusDays(5));
        req.setTargetLaycanEnd(LocalDate.now().plusDays(12));

        CharterOptimizationResponseDto res = freightService.optimizeChartering(req);

        assertNotNull(res);
        assertNotNull(res.getPlanReference());
        assertEquals("HANDYSIZE", res.getRecommendedVesselClass());
        assertTrue(res.isDraftFeasible(), "Handysize should be feasible for Haldia draft");
        assertTrue(res.getTotalLandedCostUsdTon() > 95.0, "Total landed cost must include freight and handling");
        assertTrue(res.getEstimatedCostSavingsInr() > 0.0, "Cost savings should be positive");
        System.out.println(">> [TEST 2/5] PASSED: testHaldiaProcurementOptimization");
    }

    @Test
    @DisplayName("SIH26006: Charter Optimization - Paradip Coking Coal selects Panamax and evaluates rate trajectory")
    void testParadipCokingCoalOptimization() {
        System.out.println(">> [TEST 3/5] RUNNING: testParadipCokingCoalOptimization");
        CharterOptimizationRequestDto req = new CharterOptimizationRequestDto();
        req.setCommodity("Australian Premium Hard Coking Coal");
        req.setQuantityTons(75000.0);
        req.setOriginCountry("Australia");
        req.setDestinationPortCode("INPRT");
        req.setFobPriceUsd(245.0);
        req.setTargetLaycanStart(LocalDate.now().plusDays(10));
        req.setTargetLaycanEnd(LocalDate.now().plusDays(18));

        CharterOptimizationResponseDto res = freightService.optimizeChartering(req);

        assertNotNull(res);
        assertEquals("PANAMAX", res.getRecommendedVesselClass());
        assertTrue(res.isDraftFeasible(), "Panamax should be feasible for Paradip");
        assertNotNull(res.getCharterDecision());
        assertNotNull(res.getDecisionRationale());
        assertTrue(res.getTotalLandedCostInrTon() > 0.0);
        System.out.println(">> [TEST 3/5] PASSED: testParadipCokingCoalOptimization");
    }

    @Test
    @DisplayName("SIH26006: Freight Overview - Aggregates market indices, ports, routes, and total savings")
    void testFreightOverview() {
        System.out.println(">> [TEST 4/5] RUNNING: testFreightOverview");
        FreightOverviewDto overview = freightService.getFreightOverview();

        assertNotNull(overview);
        assertNotNull(overview.getMarketData(), "Baltic Dry Index market telemetry must be available");
        assertTrue(overview.getMarketData().getBdi() > 0);
        assertTrue(overview.getTotalPorts() >= 5, "East Coast ports must be populated");
        assertTrue(overview.getTotalRoutes() >= 5, "Trade routes must be populated");
        assertTrue(overview.getTotalOptimizedSavingsInr() > 0.0, "Calculated savings must be aggregated");
        System.out.println(">> [TEST 4/5] PASSED: testFreightOverview");
    }

    @Test
    @DisplayName("SIH26006: Document Generation - Generates valid PDF dossier and multi-sheet Excel workbook")
    void testReportGeneration() {
        System.out.println(">> [TEST 5/5] RUNNING: testReportGeneration");
        List<ProcurementPlan> plans = procurementPlanRepository.findAll();
        assertFalse(plans.isEmpty(), "Seeded procurement plans must exist");

        ProcurementPlan sample = plans.get(0);

        // PDF Generation
        byte[] pdfBytes = freightReportService.generatePlanPdfDossier(sample.getId());
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 500, "PDF bytes should be non-empty valid document");
        // Verify PDF magic bytes '%PDF'
        assertEquals('%', (char) pdfBytes[0]);
        assertEquals('P', (char) pdfBytes[1]);
        assertEquals('D', (char) pdfBytes[2]);
        assertEquals('F', (char) pdfBytes[3]);

        // Excel Generation
        byte[] excelBytes = freightReportService.generateFreightExcelWorkbook();
        assertNotNull(excelBytes);
        assertTrue(excelBytes.length > 1000, "Excel workbook should contain populated sheets");
        // Verify ZIP / XLSX magic bytes 'PK'
        assertEquals('P', (char) excelBytes[0]);
        assertEquals('K', (char) excelBytes[1]);
        System.out.println(">> [TEST 5/5] PASSED: testReportGeneration");
    }
}

