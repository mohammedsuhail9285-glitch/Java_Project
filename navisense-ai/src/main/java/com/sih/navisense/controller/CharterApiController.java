package com.sih.navisense.controller;

import com.sih.navisense.dto.*;
import com.sih.navisense.entity.Port;
import com.sih.navisense.entity.TradeRoute;
import com.sih.navisense.entity.VesselType;
import com.sih.navisense.repository.PortRepository;
import com.sih.navisense.repository.TradeRouteRepository;
import com.sih.navisense.repository.VesselTypeRepository;
import com.sih.navisense.service.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CharterApiController {

    private final DashboardService dashboardService;
    private final FreightForecastService forecastService;
    private final CharterIntelligenceService charterService;
    private final ScenarioSimulationService simulationService;
    private final BacktestingService backtestingService;
    private final CopilotService copilotService;
    private final PortRepository portRepository;
    private final VesselTypeRepository vesselTypeRepository;
    private final TradeRouteRepository tradeRouteRepository;
    private final com.sih.navisense.repository.CharterDecisionRecordRepository decisionRecordRepository;

    public CharterApiController(
            DashboardService dashboardService,
            FreightForecastService forecastService,
            CharterIntelligenceService charterService,
            ScenarioSimulationService simulationService,
            BacktestingService backtestingService,
            CopilotService copilotService,
            PortRepository portRepository,
            VesselTypeRepository vesselTypeRepository,
            TradeRouteRepository tradeRouteRepository,
            com.sih.navisense.repository.CharterDecisionRecordRepository decisionRecordRepository) {
        this.dashboardService = dashboardService;
        this.forecastService = forecastService;
        this.charterService = charterService;
        this.simulationService = simulationService;
        this.backtestingService = backtestingService;
        this.copilotService = copilotService;
        this.portRepository = portRepository;
        this.vesselTypeRepository = vesselTypeRepository;
        this.tradeRouteRepository = tradeRouteRepository;
        this.decisionRecordRepository = decisionRecordRepository;
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<DashboardSummaryDto> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    @GetMapping("/freight/forecast")
    public ResponseEntity<ForecastSummaryDto> getForecast(
            @RequestParam(defaultValue = "Australia") String origin,
            @RequestParam(defaultValue = "Paradip") String destination,
            @RequestParam(defaultValue = "Panamax") String vessel) {
        return ResponseEntity.ok(forecastService.generateForecast(origin, destination, vessel));
    }

    @GetMapping("/freight/history")
    public ResponseEntity<List<HistoricalDataPointDto>> getHistoricalRates(
            @RequestParam(defaultValue = "Australia") String origin,
            @RequestParam(defaultValue = "Paradip") String destination,
            @RequestParam(defaultValue = "Panamax") String vessel) {
        ForecastSummaryDto forecast = forecastService.generateForecast(origin, destination, vessel);
        return ResponseEntity.ok(forecast.getHistoricalPoints());
    }

    @GetMapping("/ports")
    public ResponseEntity<List<Port>> getPorts(@RequestParam(required = false) String type) {
        if (type != null && !type.isBlank()) {
            return ResponseEntity.ok(portRepository.findByPortTypeIgnoreCase(type));
        }
        return ResponseEntity.ok(portRepository.findAll());
    }

    @GetMapping("/vessels")
    public ResponseEntity<List<VesselType>> getVessels() {
        return ResponseEntity.ok(vesselTypeRepository.findAll());
    }

    @GetMapping("/routes")
    public ResponseEntity<List<TradeRoute>> getRoutes() {
        return ResponseEntity.ok(tradeRouteRepository.findAll());
    }

    @PostMapping("/charter/analyze")
    public ResponseEntity<CharterDecisionResponse> analyzeCharter(@Valid @RequestBody CharterAnalysisRequest request) {
        return ResponseEntity.ok(charterService.analyzeCharter(request));
    }

    @PostMapping("/simulation")
    public ResponseEntity<SimulationResultDto> runSimulation(@RequestBody SimulationRequestDto request) {
        return ResponseEntity.ok(simulationService.simulateScenario(request));
    }

    @GetMapping("/backtest")
    public ResponseEntity<BacktestSummaryDto> getBacktest() {
        return ResponseEntity.ok(backtestingService.runBacktest());
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<AlertDto>> getAlerts() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary().getRecentAlerts());
    }

    @PostMapping("/copilot")
    public ResponseEntity<CopilotChatDto> askCopilot(@RequestBody Map<String, String> payload) {
        String q = payload.getOrDefault("query", "");
        return ResponseEntity.ok(copilotService.answerQuestion(q));
    }

    @GetMapping("/history")
    public ResponseEntity<List<com.sih.navisense.entity.CharterDecisionRecord>> getHistory(
            @RequestParam(required = false) String query) {
        List<com.sih.navisense.entity.CharterDecisionRecord> all = decisionRecordRepository.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "decisionTimestamp"));
        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase().trim();
            all = all.stream().filter(r ->
                    (r.getFingerprint() != null && r.getFingerprint().toLowerCase().contains(q)) ||
                    (r.getCargoType() != null && r.getCargoType().toLowerCase().contains(q)) ||
                    (r.getOriginCountry() != null && r.getOriginCountry().toLowerCase().contains(q)) ||
                    (r.getDestinationPort() != null && r.getDestinationPort().toLowerCase().contains(q)) ||
                    (r.getRecommendedVessel() != null && r.getRecommendedVessel().toLowerCase().contains(q))
            ).toList();
        }
        return ResponseEntity.ok(all);
    }
}
