package com.sih.navisense.service;

import com.sih.navisense.dto.ForecastPointDto;
import com.sih.navisense.dto.ForecastSummaryDto;
import com.sih.navisense.dto.HistoricalDataPointDto;
import com.sih.navisense.entity.FreightObservation;
import com.sih.navisense.entity.TradeRoute;
import com.sih.navisense.entity.VesselType;
import com.sih.navisense.repository.FreightObservationRepository;
import com.sih.navisense.repository.TradeRouteRepository;
import com.sih.navisense.repository.VesselTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FreightForecastService {

    private static final Logger log = LoggerFactory.getLogger(FreightForecastService.class);

    private final FreightObservationRepository observationRepository;
    private final TradeRouteRepository routeRepository;
    private final VesselTypeRepository vesselTypeRepository;
    private final ForecastEvaluationService evaluationService;

    // Centralized Configurable Weights & Parameters
    private final double weightExpSmoothing;
    private final double weightLinearTrend;
    private final double weightWma;
    private final double weightSeasonality;
    private final double smoothingAlpha;

    public FreightForecastService(
            FreightObservationRepository observationRepository,
            TradeRouteRepository routeRepository,
            VesselTypeRepository vesselTypeRepository,
            ForecastEvaluationService evaluationService,
            @Value("${navisense.forecast.weights.exp-smoothing:0.35}") double weightExpSmoothing,
            @Value("${navisense.forecast.weights.linear-trend:0.30}") double weightLinearTrend,
            @Value("${navisense.forecast.weights.weighted-moving-avg:0.25}") double weightWma,
            @Value("${navisense.forecast.weights.seasonality:0.10}") double weightSeasonality,
            @Value("${navisense.forecast.smoothing-alpha:0.35}") double smoothingAlpha) {
        this.observationRepository = observationRepository;
        this.routeRepository = routeRepository;
        this.vesselTypeRepository = vesselTypeRepository;
        this.evaluationService = evaluationService;
        this.weightExpSmoothing = weightExpSmoothing;
        this.weightLinearTrend = weightLinearTrend;
        this.weightWma = weightWma;
        this.weightSeasonality = weightSeasonality;
        this.smoothingAlpha = smoothingAlpha;
    }

    public ForecastSummaryDto generateForecast(String originCountry, String destinationPort, String vesselClass) {
        List<FreightObservation> observations = observationRepository
                .findByOriginCountryIgnoreCaseAndDestinationPortIgnoreCaseAndVesselClassIgnoreCaseOrderByObservationDateAsc(
                        originCountry, destinationPort, vesselClass);

        ForecastSummaryDto summary = new ForecastSummaryDto();
        summary.setOriginCountry(originCountry);
        summary.setDestinationPort(destinationPort);
        summary.setVesselClass(vesselClass);

        if (observations == null || observations.isEmpty()) {
            // Safe fallback to route base rate if no direct observations exist
            double fallbackRate = resolveFallbackRate(originCountry, destinationPort, vesselClass);
            summary.setCurrentRateUsd(fallbackRate);
            summary.setPrimaryTrend("STABLE");
            summary.setAnnualizedVolatility(15.0);
            summary.setRecommendedCharterWindow("Within next 7-14 days (Fallback estimate)");
            summary.setForecastPoints(generateSyntheticFallbackPoints(fallbackRate));
            return summary;
        }

        // Current spot rate is the latest recorded observation
        FreightObservation latest = observations.get(observations.size() - 1);
        double currentRate = latest.getRatePerTonneUsd();
        summary.setCurrentRateUsd(currentRate);

        // Historical data points for charts (sample last 90 observations to keep frontend light)
        int sliceStart = Math.max(0, observations.size() - 90);
        List<HistoricalDataPointDto> history = observations.subList(sliceStart, observations.size()).stream()
                .map(o -> new HistoricalDataPointDto(o.getObservationDate(), o.getRatePerTonneUsd()))
                .collect(Collectors.toList());
        summary.setHistoricalPoints(history);

        // Extract series of rates
        List<Double> rates = observations.stream()
                .map(FreightObservation::getRatePerTonneUsd)
                .collect(Collectors.toList());

        // Calculate volatility (daily log returns standard deviation annualized)
        double volatility = calculateVolatility(rates);
        summary.setAnnualizedVolatility(Math.round(volatility * 10.0) / 10.0);

        // Evaluate model metrics (MAE, RMSE, MAPE) vs Naive baseline
        summary.setHybridModelMetrics(evaluationService.calculateEnsembleMetrics(rates));
        summary.setNaiveBaselineMetrics(evaluationService.calculateNaiveMetrics(rates));

        // Generate Forecast Points for 7, 14, 30, 60, 90 days
        int[] horizons = {7, 14, 30, 60, 90};
        List<ForecastPointDto> points = new ArrayList<>();
        LocalDate baseDate = latest.getObservationDate();

        for (int h : horizons) {
            double predicted = predictForHorizon(rates, h);
            double stdError = (volatility / 100.0) * currentRate * Math.sqrt(h / 30.0);
            double lowerBound = Math.max(5.0, Math.round((predicted - 1.96 * stdError) * 100.0) / 100.0);
            double upperBound = Math.round((predicted + 1.96 * stdError) * 100.0) / 100.0;

            double diffPct = ((predicted - currentRate) / (currentRate > 0 ? currentRate : 1.0)) * 100.0;
            String trend = diffPct > 1.5 ? "UPWARD" : (diffPct < -1.5 ? "DOWNWARD" : "STABLE");
            int confidence = Math.max(50, Math.min(96, (int) Math.round(92.0 - (h * 0.28) - (volatility * 0.3))));

            points.add(new ForecastPointDto(
                    baseDate.plusDays(h),
                    h,
                    Math.round(predicted * 100.0) / 100.0,
                    lowerBound,
                    upperBound,
                    trend,
                    Math.round(volatility * 10.0) / 10.0,
                    confidence
            ));
        }

        summary.setForecastPoints(points);

        // Overall trend
        double thirtyDayPredicted = points.get(2).getPredictedRateUsd();
        double trend30dPct = ((thirtyDayPredicted - currentRate) / currentRate) * 100.0;
        summary.setPrimaryTrend(trend30dPct > 1.5 ? "UPWARD" : (trend30dPct < -1.5 ? "DOWNWARD" : "STABLE"));

        // Smart Charter Window determination
        summary.setRecommendedCharterWindow(calculateSmartCharterWindow(baseDate, currentRate, points));

        return summary;
    }

    public double predictForHorizon(List<Double> rates, int horizonDays) {
        if (rates == null || rates.isEmpty()) return 25.0;
        if (rates.size() == 1) return rates.get(0);

        // 1. Exponential Smoothing Forecast
        double expSmooth = calculateExponentialSmoothing(rates);

        // 2. Linear Trend Regression Forecast
        double linearTrend = calculateLinearTrend(rates, horizonDays);

        // 3. Weighted Moving Average (last 14 days)
        double wma = calculateWeightedMovingAverage(rates, 14);

        // 4. Seasonal Adjustment Factor
        double seasonalAdjustment = calculateSeasonalComponent(rates, horizonDays);

        // Ensemble Combination
        double ensemble = (weightExpSmoothing * expSmooth) +
                          (weightLinearTrend * linearTrend) +
                          (weightWma * wma) +
                          (weightSeasonality * seasonalAdjustment);

        return Math.max(5.0, Math.round(ensemble * 100.0) / 100.0);
    }

    private double calculateExponentialSmoothing(List<Double> rates) {
        double s = rates.get(0);
        for (int i = 1; i < rates.size(); i++) {
            s = (smoothingAlpha * rates.get(i)) + ((1.0 - smoothingAlpha) * s);
        }
        return s;
    }

    private double calculateLinearTrend(List<Double> rates, int horizonDays) {
        int n = Math.min(rates.size(), 60); // Use last 60 observations for trend
        int offset = rates.size() - n;

        double sumX = 0.0;
        double sumY = 0.0;
        double sumXY = 0.0;
        double sumX2 = 0.0;

        for (int i = 0; i < n; i++) {
            double x = i + 1;
            double y = rates.get(offset + i);
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double denominator = (n * sumX2) - (sumX * sumX);
        if (Math.abs(denominator) < 1e-9) {
            return rates.get(rates.size() - 1);
        }

        double slope = ((n * sumXY) - (sumX * sumY)) / denominator;
        double intercept = (sumY - (slope * sumX)) / n;

        double targetX = n + horizonDays;
        return intercept + (slope * targetX);
    }

    private double calculateWeightedMovingAverage(List<Double> rates, int window) {
        int k = Math.min(rates.size(), window);
        int offset = rates.size() - k;

        double weightedSum = 0.0;
        double weightTotal = 0.0;

        for (int i = 0; i < k; i++) {
            double weight = i + 1; // 1 to k
            weightedSum += rates.get(offset + i) * weight;
            weightTotal += weight;
        }

        return weightTotal > 0 ? (weightedSum / weightTotal) : rates.get(rates.size() - 1);
    }

    private double calculateSeasonalComponent(List<Double> rates, int horizonDays) {
        double latestRate = rates.get(rates.size() - 1);
        // Sinusoidal seasonal adjustment component
        double seasonalAngle = ((horizonDays % 365) / 365.0) * 2.0 * Math.PI;
        double adjustment = latestRate * (1.0 + 0.04 * Math.sin(seasonalAngle));
        return adjustment;
    }

    public double calculateVolatility(List<Double> rates) {
        if (rates == null || rates.size() < 2) return 15.0;

        List<Double> returns = new ArrayList<>();
        for (int i = 1; i < rates.size(); i++) {
            double prev = rates.get(i - 1);
            double curr = rates.get(i);
            if (prev > 0 && curr > 0) {
                returns.add(Math.log(curr / prev));
            }
        }

        if (returns.isEmpty()) return 15.0;

        double mean = returns.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = returns.stream()
                .mapToDouble(r -> Math.pow(r - mean, 2))
                .sum() / (returns.size() > 1 ? returns.size() - 1 : 1);

        double dailyStd = Math.sqrt(variance);
        double annualized = dailyStd * Math.sqrt(252.0) * 100.0; // percentage
        return Math.max(5.0, Math.min(65.0, annualized));
    }

    private String calculateSmartCharterWindow(LocalDate baseDate, double currentRate, List<ForecastPointDto> points) {
        if (points == null || points.size() < 3) {
            LocalDate start = baseDate.plusDays(3);
            LocalDate end = baseDate.plusDays(10);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
            return start.format(fmt) + " – " + end.format(fmt);
        }

        ForecastPointDto p7 = points.get(0);
        ForecastPointDto p14 = points.get(1);
        ForecastPointDto p30 = points.get(2);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

        // If rate is expected to rise sharply, book immediately in days 2 to 7
        if (p14.getPredictedRateUsd() > currentRate * 1.03) {
            LocalDate start = baseDate.plusDays(2);
            LocalDate end = baseDate.plusDays(7);
            return start.format(fmt) + " – " + end.format(fmt) + " (Rate rising: Book promptly)";
        }

        // If rate is expected to decline in 14-30 days, wait for the dip
        if (p14.getPredictedRateUsd() < currentRate * 0.97 || p30.getPredictedRateUsd() < currentRate * 0.97) {
            LocalDate start = baseDate.plusDays(12);
            LocalDate end = baseDate.plusDays(20);
            return start.format(fmt) + " – " + end.format(fmt) + " (Rate softening: Await window)";
        }

        // Otherwise, balanced optimal procurement window
        LocalDate start = baseDate.plusDays(5);
        LocalDate end = baseDate.plusDays(12);
        return start.format(fmt) + " – " + end.format(fmt) + " (Stable window)";
    }

    private double resolveFallbackRate(String origin, String dest, String vessel) {
        Optional<TradeRoute> route = routeRepository.findByOriginCountryIgnoreCase(origin).stream()
                .filter(r -> r.getDestinationPort().equalsIgnoreCase(dest))
                .findFirst();

        double base = route.map(TradeRoute::getBaseFreightUsdPerTonne).orElse(24.50);
        Optional<VesselType> vt = vesselTypeRepository.findByNameIgnoreCase(vessel);
        double factor = vt.map(VesselType::getCharterCostFactor).orElse(1.0);
        return Math.round(base * factor * 100.0) / 100.0;
    }

    private List<ForecastPointDto> generateSyntheticFallbackPoints(double rate) {
        List<ForecastPointDto> list = new ArrayList<>();
        LocalDate now = LocalDate.now();
        int[] horizons = {7, 14, 30, 60, 90};
        for (int h : horizons) {
            list.add(new ForecastPointDto(
                    now.plusDays(h), h, rate,
                    Math.round(rate * 0.92 * 100.0) / 100.0,
                    Math.round(rate * 1.08 * 100.0) / 100.0,
                    "STABLE", 15.0, 80));
        }
        return list;
    }
}
