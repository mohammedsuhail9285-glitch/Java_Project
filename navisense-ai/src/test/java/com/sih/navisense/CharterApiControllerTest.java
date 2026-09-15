package com.sih.navisense;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sih.navisense.dto.CharterAnalysisRequest;
import com.sih.navisense.dto.SimulationRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CharterApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/dashboard/summary should return 200 with complete KPIs")
    void testGetDashboardSummary() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currentFreightIndex").exists())
                .andExpect(jsonPath("$.recentAlerts").isArray())
                .andExpect(jsonPath("$.routeHeatmaps").isArray());
    }

    @Test
    @DisplayName("GET /api/freight/forecast should return 200 with forecast points")
    void testGetFreightForecast() throws Exception {
        mockMvc.perform(get("/api/freight/forecast")
                        .param("origin", "Australia")
                        .param("destination", "Paradip")
                        .param("vessel", "Panamax"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.forecastPoints").isArray())
                .andExpect(jsonPath("$.hybridModelMetrics").exists());
    }

    @Test
    @DisplayName("POST /api/charter/analyze should return 200 with complete decision")
    void testAnalyzeCharterApi() throws Exception {
        CharterAnalysisRequest req = new CharterAnalysisRequest();
        req.setCargoType("Coking Coal");
        req.setQuantityTonnes(150000.0);
        req.setOriginCountry("Australia");
        req.setLoadingPort("Hay Point");
        req.setDestinationPort("Paradip");
        req.setShipmentDate(LocalDate.now().plusDays(14));
        req.setContractDurationMonths(3);

        mockMvc.perform(post("/api/charter/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fingerprint").exists())
                .andExpect(jsonPath("$.primaryDecision").exists())
                .andExpect(jsonPath("$.recommendedVessel").value("Panamax"));
    }

    @Test
    @DisplayName("POST /api/simulation should return 200 with comparative deltas")
    void testSimulationApi() throws Exception {
        SimulationRequestDto sim = new SimulationRequestDto();
        sim.setFreightChangePct(10.0);
        sim.setCongestionChangePct(25.0);

        mockMvc.perform(post("/api/simulation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sim)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.costDeltaInr").exists())
                .andExpect(jsonPath("$.keyInsight").exists());
    }

    @Test
    @DisplayName("POST /api/copilot should answer domain questions without external LLM")
    void testCopilotApi() throws Exception {
        mockMvc.perform(post("/api/copilot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("query", "Why was Capesize rejected?"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").exists())
                .andExpect(jsonPath("$.category").value("PORT_COMPATIBILITY"));
    }
}
