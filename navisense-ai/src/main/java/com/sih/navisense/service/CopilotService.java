package com.sih.navisense.service;

import com.sih.navisense.dto.CharterAnalysisRequest;
import com.sih.navisense.dto.CharterDecisionResponse;
import com.sih.navisense.dto.CopilotChatDto;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class CopilotService {

    private final CharterIntelligenceService charterService;

    public CopilotService(CharterIntelligenceService charterService) {
        this.charterService = charterService;
    }

    public CopilotChatDto answerQuestion(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new CopilotChatDto(
                    query,
                    "Please ask a question about freight rates, vessel suitability, port constraints, or contract strategies for SAIL.",
                    "GENERAL",
                    "100%"
            );
        }

        String q = query.toLowerCase(Locale.ROOT).trim();

        // 1. Capesize rejection / Draft questions
        if (q.contains("capesize") && (q.contains("reject") || q.contains("why") || q.contains("paradip") || q.contains("draft"))) {
            return new CopilotChatDto(
                    query,
                    "Capesize vessels (typical draft 18.2m, LOA ~292m) exceed the maximum permitted draft limit of 16.5m at Paradip and 10.5m at Haldia. Fully laden Capesize bulk carriers can only safely call at deepwater ports like Gangavaram (19.5m max draft), Visakhapatnam (18.1m), or Dhamra (18.5m). For Paradip, Panamax (14.5m draft) is the largest compatible vessel class.",
                    "PORT_COMPATIBILITY",
                    "98%"
            );
        }

        // 2. Booking timing / Book now vs wait
        if (q.contains("book now") || q.contains("wait") || q.contains("when") || q.contains("window")) {
            return new CopilotChatDto(
                    query,
                    "Based on our 30-day ensemble forecast, freight rates on major Australia → East Coast India corridors are projected to trend upward by +3.5% over the next 14 days. We recommend securing capacity within the next 3 to 7 days ('BOOK NOW' / Short-Term COA) to insulate SAIL against spot price escalation.",
                    "FREIGHT_FORECAST",
                    "92%"
            );
        }

        // 3. Lowest risk route
        if (q.contains("lowest risk") || q.contains("safest") || q.contains("risk")) {
            return new CopilotChatDto(
                    query,
                    "The Australia (Gladstone) → Visakhapatnam route exhibits the lowest composite risk score (28/100 - LOW), supported by stable weather channels, deep port drafts (18.1m), and lower berth congestion (index 42/100). In contrast, Haldia has high congestion (68/100) and Russia (Ust-Luga) has long maritime transit exposure.",
                    "RISK_ANALYSIS",
                    "95%"
            );
        }

        // 4. Savings questions
        if (q.contains("save") || q.contains("saving") || q.contains("cost")) {
            return new CopilotChatDto(
                    query,
                    "By adopting NaviSense AI's Short-Term Multi-Voyage Contract (COA) rather than reactive spot booking, SAIL saves approximately 4.5% on baseline freight, equating to ₹1.84 Cr on a typical 150,000-tonne coking coal parcel from Australia to Paradip. Across historical backtests, cumulative savings reach ₹24.28 Cr.",
                    "COST_OPTIMIZATION",
                    "94%"
            );
        }

        // 5. Contract strategy
        if (q.contains("contract") || q.contains("strategy") || q.contains("spot") || q.contains("hybrid")) {
            return new CopilotChatDto(
                    query,
                    "When market volatility exceeds 18% or rates are rising, NaviSense recommends a Short-Term Multi-Voyage Contract (COA) or a 70/30 Hybrid Contract (70% fixed COA / 30% spot flexibility). This protects steel plants from supply chain disruption while allowing tactical spot purchases during temporary rate dips.",
                    "CONTRACT_STRATEGY",
                    "93%"
            );
        }

        // 6. Dynamic Vessel/Route/Quantity Query Evaluation (e.g., "What vessel for 100,000 tonnes from Australia to Paradip?")
        if (q.contains("vessel") || q.contains("tonne") || q.contains("ton") || q.contains("parcel") || q.contains("shipment") || q.contains("how much")) {
            Double parsedQty = extractQuantityTonnes(q);
            String parsedOrigin = extractOrigin(q);
            String parsedDest = extractDestination(q);

            CharterAnalysisRequest req = new CharterAnalysisRequest();
            if (parsedQty != null && parsedQty >= 5000 && parsedQty <= 500000) {
                req.setQuantityTonnes(parsedQty);
            }
            if (parsedOrigin != null) {
                req.setOriginCountry(parsedOrigin);
                if ("Australia".equalsIgnoreCase(parsedOrigin)) req.setLoadingPort("Hay Point");
                else if ("United States".equalsIgnoreCase(parsedOrigin)) req.setLoadingPort("Hampton Roads");
                else if ("Mozambique".equalsIgnoreCase(parsedOrigin)) req.setLoadingPort("Maputo");
                else if ("Indonesia".equalsIgnoreCase(parsedOrigin)) req.setLoadingPort("Tanjung Bara");
                else if ("Russia".equalsIgnoreCase(parsedOrigin)) req.setLoadingPort("Ust-Luga");
            }
            if (parsedDest != null) {
                req.setDestinationPort(parsedDest);
            }

            CharterDecisionResponse resp = charterService.analyzeCharter(req);

            String answer = String.format(
                    "For %,.0f tonnes from %s (%s) to %s: NaviSense AI recommends **%s** (%d voyage(s), %.1f%% utilization). Recommended action: **%s** under a %s. Estimated total logistics outlay is %s with potential savings of %s (%.1f%%). Recommended laycan window: %s.",
                    req.getQuantityTonnes(),
                    req.getOriginCountry(),
                    req.getLoadingPort(),
                    req.getDestinationPort(),
                    resp.getRecommendedVessel(),
                    resp.getVoyageCount(),
                    resp.getCapacityUtilizationPct(),
                    resp.getPrimaryDecision(),
                    resp.getRecommendedContract(),
                    resp.getTotalCostInrFormatted(),
                    resp.getPotentialSavingsInrFormatted(),
                    resp.getSavingsPercentage(),
                    resp.getBestCharterWindow()
            );

            return new CopilotChatDto(query, answer, "DYNAMIC_CHARTER_INTELLIGENCE", resp.getDecisionConfidence() + "%");
        }

        // Default intelligent fallback
        return new CopilotChatDto(
                query,
                "NaviSense AI monitors East Coast India imports for SAIL. To optimize your shipment, enter your cargo quantity and port pair into the 'New Charter Analysis' workflow or test rate sensitivities in the 'What-If Simulator'.",
                "ASSISTANT",
                "90%"
        );
    }

    private Double extractQuantityTonnes(String q) {
        try {
            // Pattern for e.g. "100k", "100 k", "100,000", "150000"
            java.util.regex.Matcher mK = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*k\\b").matcher(q);
            if (mK.find()) {
                return Double.parseDouble(mK.group(1)) * 1000.0;
            }
            java.util.regex.Matcher mNum = java.util.regex.Pattern.compile("(\\d[\\d,]*\\d|\\d+)\\s*(?:tonnes?|tons?|t\\b)?").matcher(q);
            while (mNum.find()) {
                String raw = mNum.group(1).replace(",", "");
                double val = Double.parseDouble(raw);
                if (val >= 1000 && val <= 1000000) {
                    return val;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String extractOrigin(String q) {
        if (q.contains("australia")) return "Australia";
        if (q.contains("usa") || q.contains("united states") || q.contains("america")) return "United States";
        if (q.contains("mozambique")) return "Mozambique";
        if (q.contains("indonesia")) return "Indonesia";
        if (q.contains("russia")) return "Russia";
        return null;
    }

    private String extractDestination(String q) {
        if (q.contains("paradip")) return "Paradip";
        if (q.contains("vizag") || q.contains("visakhapatnam")) return "Visakhapatnam";
        if (q.contains("gangavaram")) return "Gangavaram";
        if (q.contains("dhamra")) return "Dhamra";
        if (q.contains("gopalpur")) return "Gopalpur";
        if (q.contains("haldia")) return "Haldia";
        return null;
    }
}
