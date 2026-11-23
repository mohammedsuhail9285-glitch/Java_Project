package com.example.lmchecker.controller;

import com.example.lmchecker.dto.ApiResponse;
import com.example.lmchecker.dto.CharterOptimizationRequestDto;
import com.example.lmchecker.dto.CharterOptimizationResponseDto;
import com.example.lmchecker.dto.FreightOverviewDto;
import com.example.lmchecker.entity.*;
import com.example.lmchecker.service.FreightReportService;
import com.example.lmchecker.service.FreightService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/freight")
public class FreightController {

    private final FreightService freightService;
    private final FreightReportService freightReportService;

    public FreightController(FreightService freightService, FreightReportService freightReportService) {
        this.freightService = freightService;
        this.freightReportService = freightReportService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<FreightOverviewDto>> getOverview() {
        return ResponseEntity.ok(ApiResponse.ok("Freight intelligence overview retrieved", freightService.getFreightOverview()));
    }

    @GetMapping("/ports")
    public ResponseEntity<ApiResponse<List<MaritimePort>>> getPorts() {
        return ResponseEntity.ok(ApiResponse.ok("Maritime ports retrieved", freightService.getAllPorts()));
    }

    @GetMapping("/vessels")
    public ResponseEntity<ApiResponse<List<VesselClass>>> getVessels() {
        return ResponseEntity.ok(ApiResponse.ok("Vessel classes retrieved", freightService.getAllVesselClasses()));
    }

    @GetMapping("/routes")
    public ResponseEntity<ApiResponse<List<TradeRoute>>> getRoutes() {
        return ResponseEntity.ok(ApiResponse.ok("Trade routes retrieved", freightService.getAllTradeRoutes()));
    }

    @GetMapping("/forecasts")
    public ResponseEntity<ApiResponse<List<FreightForecast>>> getForecasts() {
        return ResponseEntity.ok(ApiResponse.ok("Freight forecasts retrieved", freightService.getAllForecasts()));
    }

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<ProcurementPlan>>> getPlans() {
        return ResponseEntity.ok(ApiResponse.ok("Procurement plans retrieved", freightService.getAllProcurementPlans()));
    }

    @GetMapping("/plans/{ref}")
    public ResponseEntity<ApiResponse<ProcurementPlan>> getPlanByRef(@PathVariable String ref) {
        return freightService.getPlanByReference(ref)
                .map(p -> ResponseEntity.ok(ApiResponse.ok("Procurement plan retrieved", p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping({"/optimize", "/chartering/optimize"})
    public ResponseEntity<ApiResponse<CharterOptimizationResponseDto>> optimizeCharter(
            @RequestBody CharterOptimizationRequestDto request) {
        CharterOptimizationResponseDto result = freightService.optimizeChartering(request);
        return ResponseEntity.ok(ApiResponse.ok("Chartering strategy optimized successfully", result));
    }

    @GetMapping("/plans/{id}/pdf")
    public ResponseEntity<byte[]> downloadPlanPdf(@PathVariable Long id) {
        byte[] pdfBytes = freightReportService.generatePlanPdfDossier(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "SIH26006_Procurement_Dossier_" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> downloadExcelWorkbook() {
        byte[] excelBytes = freightReportService.generateFreightExcelWorkbook();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "SIH26006_Freight_Chartering_Master.xlsx");
        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> downloadCsvReport() {
        byte[] csvBytes = freightReportService.generateFreightCsvReport();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "SIH26006_Freight_Procurement_Plans.csv");
        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }

    @GetMapping("/model/accuracy")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getModelAccuracy() {
        Map<String, Object> accuracy = freightService.getModelAccuracy();
        return ResponseEntity.ok(ApiResponse.ok("Empirical model holdout accuracy metrics retrieved", accuracy));
    }
}
