package com.sih.navisense.controller;

import com.sih.navisense.dto.CharterAnalysisRequest;
import com.sih.navisense.dto.CharterDecisionResponse;
import com.sih.navisense.dto.DashboardSummaryDto;
import com.sih.navisense.dto.ForecastSummaryDto;
import com.sih.navisense.dto.SimulationRequestDto;
import com.sih.navisense.entity.CharterDecisionRecord;
import com.sih.navisense.entity.Port;
import com.sih.navisense.entity.TradeRoute;
import com.sih.navisense.entity.VesselType;
import com.sih.navisense.repository.*;
import com.sih.navisense.service.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
public class PageController {

    private final DashboardService dashboardService;
    private final FreightForecastService forecastService;
    private final CharterIntelligenceService charterService;
    private final ScenarioSimulationService simulationService;
    private final BacktestingService backtestingService;
    private final PortRepository portRepository;
    private final VesselTypeRepository vesselTypeRepository;
    private final TradeRouteRepository tradeRouteRepository;
    private final FreightObservationRepository observationRepository;
    private final CharterDecisionRecordRepository decisionRecordRepository;

    public PageController(
            DashboardService dashboardService,
            FreightForecastService forecastService,
            CharterIntelligenceService charterService,
            ScenarioSimulationService simulationService,
            BacktestingService backtestingService,
            PortRepository portRepository,
            VesselTypeRepository vesselTypeRepository,
            TradeRouteRepository tradeRouteRepository,
            FreightObservationRepository observationRepository,
            CharterDecisionRecordRepository decisionRecordRepository) {
        this.dashboardService = dashboardService;
        this.forecastService = forecastService;
        this.charterService = charterService;
        this.simulationService = simulationService;
        this.backtestingService = backtestingService;
        this.portRepository = portRepository;
        this.vesselTypeRepository = vesselTypeRepository;
        this.tradeRouteRepository = tradeRouteRepository;
        this.observationRepository = observationRepository;
        this.decisionRecordRepository = decisionRecordRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        return dashboard(model);
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activeNav", "dashboard");
        DashboardSummaryDto summary = dashboardService.getDashboardSummary();
        model.addAttribute("summary", summary);

        // Fetch or compute latest decision for the prominent Executive Decision Card
        CharterDecisionResponse executiveDecision;
        List<CharterDecisionRecord> all = decisionRecordRepository.findAll(Sort.by(Sort.Direction.DESC, "decisionTimestamp"));
        if (!all.isEmpty()) {
            executiveDecision = charterService.getDecisionByFingerprint(all.get(0).getFingerprint())
                    .orElseGet(() -> charterService.analyzeCharter(new CharterAnalysisRequest()));
        } else {
            executiveDecision = charterService.analyzeCharter(new CharterAnalysisRequest());
        }
        model.addAttribute("executiveDecision", executiveDecision);

        return "dashboard";
    }

    @GetMapping("/charter")
    public String charterForm(@RequestParam(required = false) String scenario, Model model) {
        CharterAnalysisRequest req = new CharterAnalysisRequest();

        // 4 Pre-configured SIH Demo Scenarios
        if ("1".equals(scenario)) {
            // Scenario 1: Australia -> Paradip (150k t Coking Coal)
            req.setCargoType("Coking Coal");
            req.setQuantityTonnes(150000.0);
            req.setOriginCountry("Australia");
            req.setLoadingPort("Hay Point");
            req.setDestinationPort("Paradip");
            req.setPreferredVessel("Panamax");
            req.setBudgetInr(400000000.0); // ₹40.0 Cr
            req.setExpectedFreightRateUsd(26.0);
        } else if ("2".equals(scenario)) {
            // Scenario 2: Mozambique -> Dhamra (80k t)
            req.setCargoType("Coking Coal");
            req.setQuantityTonnes(80000.0);
            req.setOriginCountry("Mozambique");
            req.setLoadingPort("Maputo");
            req.setDestinationPort("Dhamra");
            req.setPreferredVessel("Supramax");
            req.setBudgetInr(220000000.0); // ₹22.0 Cr
            req.setExpectedFreightRateUsd(27.0);
        } else if ("3".equals(scenario)) {
            // Scenario 3: Indonesia -> Haldia (55k t)
            req.setCargoType("Thermal Coal");
            req.setQuantityTonnes(55000.0);
            req.setOriginCountry("Indonesia");
            req.setLoadingPort("Tanjung Bara");
            req.setDestinationPort("Haldia");
            req.setPreferredVessel("Handysize");
            req.setBudgetInr(160000000.0); // ₹16.0 Cr
            req.setExpectedFreightRateUsd(29.0);
        } else if ("4".equals(scenario)) {
            // Scenario 4: USA -> Visakhapatnam (180k t)
            req.setCargoType("Hard Coking Coal");
            req.setQuantityTonnes(180000.0);
            req.setOriginCountry("United States");
            req.setLoadingPort("Hampton Roads");
            req.setDestinationPort("Visakhapatnam");
            req.setPreferredVessel("Capesize");
            req.setBudgetInr(480000000.0); // ₹48.0 Cr
            req.setExpectedFreightRateUsd(50.0);
        }

        populateDropdowns(model);
        model.addAttribute("charterRequest", req);
        model.addAttribute("activeNav", "charter");
        return "charter-analysis";
    }

    @PostMapping("/charter/analyze")
    public String runCharterAnalysis(
            @Valid @ModelAttribute("charterRequest") CharterAnalysisRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            populateDropdowns(model);
            model.addAttribute("activeNav", "charter");
            return "charter-analysis";
        }

        CharterDecisionResponse decision = charterService.analyzeCharter(request);
        return "redirect:/charter/result/" + decision.getFingerprint();
    }

    @GetMapping("/charter/result/{fingerprint}")
    public String charterResult(@PathVariable String fingerprint, Model model) {
        Optional<CharterDecisionResponse> decisionOpt = charterService.getDecisionByFingerprint(fingerprint);
        if (decisionOpt.isEmpty()) {
            // Fallback: run default analysis
            CharterAnalysisRequest req = new CharterAnalysisRequest();
            CharterDecisionResponse defaultDecision = charterService.analyzeCharter(req);
            model.addAttribute("decision", defaultDecision);
        } else {
            model.addAttribute("decision", decisionOpt.get());
        }
        model.addAttribute("activeNav", "charter");
        return "charter-result";
    }

    @GetMapping("/charter/report/{fingerprint}")
    public String charterReport(@PathVariable String fingerprint, Model model) {
        Optional<CharterDecisionResponse> decisionOpt = charterService.getDecisionByFingerprint(fingerprint);
        CharterDecisionResponse decision = decisionOpt.orElseGet(() -> charterService.analyzeCharter(new CharterAnalysisRequest()));
        model.addAttribute("decision", decision);
        return "report";
    }

    @GetMapping("/forecast")
    public String forecast(
            @RequestParam(defaultValue = "Australia") String origin,
            @RequestParam(defaultValue = "Paradip") String destination,
            @RequestParam(defaultValue = "Panamax") String vessel,
            Model model) {

        ForecastSummaryDto summary = forecastService.generateForecast(origin, destination, vessel);
        model.addAttribute("forecast", summary);
        model.addAttribute("origin", origin);
        model.addAttribute("destination", destination);
        model.addAttribute("vessel", vessel);
        populateDropdowns(model);
        model.addAttribute("activeNav", "forecast");
        return "forecast";
    }

    @GetMapping("/vessels")
    public String vessels(Model model) {
        model.addAttribute("vessels", vesselTypeRepository.findAll());
        model.addAttribute("activeNav", "vessels");
        return "vessel-optimizer";
    }

    @GetMapping("/ports")
    public String ports(Model model) {
        model.addAttribute("ports", portRepository.findAll());
        model.addAttribute("activeNav", "ports");
        return "ports";
    }

    @GetMapping("/routes")
    public String routes(Model model) {
        model.addAttribute("routes", tradeRouteRepository.findAll());
        model.addAttribute("heatmap", dashboardService.getDashboardSummary().getRouteHeatmaps());
        model.addAttribute("activeNav", "routes");
        return "routes";
    }

    @GetMapping("/simulation")
    public String simulation(Model model) {
        SimulationRequestDto defaultSim = new SimulationRequestDto();
        model.addAttribute("simRequest", defaultSim);
        model.addAttribute("simResult", simulationService.simulateScenario(defaultSim));
        model.addAttribute("activeNav", "simulation");
        populateDropdowns(model);
        return "simulation";
    }

    @PostMapping("/simulation")
    public String runSimulation(@ModelAttribute("simRequest") SimulationRequestDto request, Model model) {
        model.addAttribute("simRequest", request);
        model.addAttribute("simResult", simulationService.simulateScenario(request));
        model.addAttribute("activeNav", "simulation");
        populateDropdowns(model);
        return "simulation";
    }

    @GetMapping("/risk")
    public String riskMonitor(Model model) {
        model.addAttribute("summary", dashboardService.getDashboardSummary());
        model.addAttribute("activeNav", "risk");
        return "risk-monitor";
    }

    @GetMapping("/contract-strategy")
    public String contractStrategy(Model model) {
        CharterAnalysisRequest req = new CharterAnalysisRequest();
        CharterDecisionResponse decision = charterService.analyzeCharter(req);
        model.addAttribute("decision", decision);
        model.addAttribute("activeNav", "contracts");
        return "contract-strategy";
    }

    @GetMapping("/backtest")
    public String backtest(Model model) {
        model.addAttribute("backtest", backtestingService.runBacktest());
        model.addAttribute("activeNav", "backtest");
        return "backtest";
    }

    @GetMapping("/copilot")
    public String copilot(Model model) {
        model.addAttribute("activeNav", "copilot");
        return "copilot";
    }

    @GetMapping("/history")
    public String decisionHistory(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String verdict,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(defaultValue = "date_desc") String sortBy,
            Model model) {

        List<CharterDecisionRecord> list = decisionRecordRepository.findAll();

        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase().trim();
            list = list.stream().filter(r ->
                    (r.getFingerprint() != null && r.getFingerprint().toLowerCase().contains(q)) ||
                    (r.getCargoType() != null && r.getCargoType().toLowerCase().contains(q)) ||
                    (r.getOriginCountry() != null && r.getOriginCountry().toLowerCase().contains(q)) ||
                    (r.getDestinationPort() != null && r.getDestinationPort().toLowerCase().contains(q)) ||
                    (r.getRecommendedVessel() != null && r.getRecommendedVessel().toLowerCase().contains(q))
            ).toList();
        }

        if (verdict != null && !verdict.isBlank()) {
            list = list.stream().filter(r -> verdict.equalsIgnoreCase(r.getPrimaryDecision())).toList();
        }

        if (riskLevel != null && !riskLevel.isBlank()) {
            list = list.stream().filter(r -> riskLevel.equalsIgnoreCase(r.getRiskLevel())).toList();
        }

        List<CharterDecisionRecord> sortedList = new ArrayList<>(list);
        switch (sortBy) {
            case "date_asc" -> sortedList.sort(Comparator.comparing(CharterDecisionRecord::getDecisionTimestamp));
            case "cost_desc" -> sortedList.sort(Comparator.comparing(CharterDecisionRecord::getTotalCostInr).reversed());
            case "savings_desc" -> sortedList.sort(Comparator.comparing(CharterDecisionRecord::getPotentialSavingsInr).reversed());
            default -> sortedList.sort(Comparator.comparing(CharterDecisionRecord::getDecisionTimestamp).reversed());
        }

        model.addAttribute("decisions", sortedList);
        model.addAttribute("totalCount", sortedList.size());
        model.addAttribute("query", query);
        model.addAttribute("verdict", verdict);
        model.addAttribute("riskLevel", riskLevel);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("activeNav", "history");
        return "history";
    }

    @GetMapping("/alerts")
    public String alertsView(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String severity,
            Model model) {
        var allAlerts = dashboardService.getDashboardSummary().getRecentAlerts();
        var filteredAlerts = allAlerts.stream()
                .filter(a -> category == null || category.isBlank() || category.equalsIgnoreCase(a.getCategory()))
                .filter(a -> severity == null || severity.isBlank() || severity.equalsIgnoreCase(a.getSeverity()))
                .toList();

        model.addAttribute("alerts", filteredAlerts);
        model.addAttribute("totalAlerts", allAlerts.size());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedSeverity", severity);
        model.addAttribute("activeNav", "alerts");
        return "alerts";
    }

    @GetMapping("/settings")
    public String settingsView(Model model) {
        model.addAttribute("activeNav", "settings");
        model.addAttribute("usdInrRate", 85.50);
        model.addAttribute("bunkerPriceUsd", 620.00);
        model.addAttribute("demurrageDailyUsd", 18500.00);
        model.addAttribute("portTariffUsd", 3.25);
        return "settings";
    }

    @GetMapping("/data-lab")
    public String dataLab(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String vessel,
            Model model) {
        model.addAttribute("ports", portRepository.findAll());
        model.addAttribute("vessels", vesselTypeRepository.findAll());
        model.addAttribute("routes", tradeRouteRepository.findAll());

        var obsList = observationRepository.findAll(PageRequest.of(0, 100)).getContent();
        if (origin != null && !origin.isBlank()) {
            obsList = obsList.stream().filter(o -> origin.equalsIgnoreCase(o.getOriginCountry())).toList();
        }
        if (destination != null && !destination.isBlank()) {
            obsList = obsList.stream().filter(o -> destination.equalsIgnoreCase(o.getDestinationPort())).toList();
        }
        if (vessel != null && !vessel.isBlank()) {
            obsList = obsList.stream().filter(o -> vessel.equalsIgnoreCase(o.getVesselClass())).toList();
        }

        model.addAttribute("observations", obsList);
        model.addAttribute("totalObservations", observationRepository.count());
        model.addAttribute("selectedOrigin", origin);
        model.addAttribute("selectedDestination", destination);
        model.addAttribute("selectedVessel", vessel);
        populateDropdowns(model);
        model.addAttribute("activeNav", "datalab");
        return "data-lab";
    }

    @GetMapping("/demo")
    public String demoGuide(Model model) {
        model.addAttribute("activeNav", "demo");
        return "demo";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("activeNav", "about");
        return "about";
    }

    private void populateDropdowns(Model model) {
        model.addAttribute("origins", List.of("Australia", "United States", "Mozambique", "Russia", "Indonesia"));
        model.addAttribute("loadingPorts", portRepository.findByPortTypeIgnoreCase("LOADING"));
        model.addAttribute("destinationPorts", portRepository.findByPortTypeIgnoreCase("DESTINATION"));
        model.addAttribute("vesselTypes", vesselTypeRepository.findAll());
    }
}
