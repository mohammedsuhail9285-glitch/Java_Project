package com.example.lmchecker.controller;

import com.example.lmchecker.dto.DashboardSummaryDto;
import com.example.lmchecker.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    @GetMapping("/trends")
    public ResponseEntity<List<DashboardSummaryDto.TrendDataPointDto>> getTrends() {
        return ResponseEntity.ok(dashboardService.getSummary().getComplianceTrends());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<DashboardSummaryDto.CategoryRiskDto>> getCategoryRisks() {
        return ResponseEntity.ok(dashboardService.getSummary().getCategoryRisks());
    }

    @GetMapping("/sellers")
    public ResponseEntity<List<DashboardSummaryDto.SellerRiskDto>> getSellerRisks() {
        return ResponseEntity.ok(dashboardService.getSummary().getSellerRisks());
    }
}
