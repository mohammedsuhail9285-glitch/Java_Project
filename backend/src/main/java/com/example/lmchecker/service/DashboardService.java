package com.example.lmchecker.service;

import com.example.lmchecker.dto.DashboardSummaryDto;
import com.example.lmchecker.entity.*;
import com.example.lmchecker.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardService {

    private final ProductRepository productRepository;
    private final ViolationRepository violationRepository;
    private final ScanJobRepository scanJobRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;

    public DashboardService(ProductRepository productRepository,
                            ViolationRepository violationRepository,
                            ScanJobRepository scanJobRepository,
                            CategoryRepository categoryRepository,
                            SellerRepository sellerRepository) {
        this.productRepository = productRepository;
        this.violationRepository = violationRepository;
        this.scanJobRepository = scanJobRepository;
        this.categoryRepository = categoryRepository;
        this.sellerRepository = sellerRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryDto getSummary() {
        DashboardSummaryDto dto = new DashboardSummaryDto();

        List<Product> products = productRepository.findAll();
        long totalProducts = products.size();
        long compliant = 0;
        long nonCompliant = 0;
        long underReview = 0;
        long pending = 0;

        for (Product p : products) {
            String s = p.getStatus() != null ? p.getStatus().toUpperCase() : "";
            if ("PASSED_AUTOMATED_CHECKS".equals(s)) {
                compliant++;
            } else if ("POTENTIAL_NON_COMPLIANT".equals(s)) {
                nonCompliant++;
            } else if ("REQUIRES_REVIEW".equals(s)) {
                underReview++;
            } else if ("PENDING_SCAN".equals(s)) {
                pending++;
            } else {
                underReview++;
            }
        }

        dto.setTotalProducts(totalProducts);
        dto.setCompliantProducts(compliant);
        dto.setPotentialNonCompliantProducts(nonCompliant);
        dto.setRequiresReviewProducts(underReview);
        dto.setPendingScanProducts(pending);
        dto.setOverallComplianceRate(totalProducts > 0 ? Math.round(((double) compliant / totalProducts) * 1000.0) / 10.0 : 100.0);

        // Violations
        dto.setTotalViolations(violationRepository.count());
        dto.setCriticalViolations(violationRepository.countBySeverity(Severity.CRITICAL));
        dto.setMajorViolations(violationRepository.countBySeverity(Severity.MAJOR));
        dto.setMinorViolations(violationRepository.countBySeverity(Severity.MINOR));

        // Real Violation Distribution by Rule Category
        Map<String, Long> dist = new LinkedHashMap<>();
        dist.put("MRP Declarations", 0L);
        dist.put("Net Quantity", 0L);
        dist.put("Manufacturer / Packer", 0L);
        dist.put("Country of Origin", 0L);
        dist.put("Consumer Care", 0L);
        dist.put("Date of Mfg / Expiry", 0L);
        dist.put("Standard Unit of Measure", 0L);
        dist.put("E-Commerce Marketplace Parity", 0L);

        List<Violation> allViolations = violationRepository.findAll();
        for (Violation v : allViolations) {
            String ruleId = v.getRuleId() != null ? v.getRuleId().trim().toUpperCase() : "";
            if ("LM003".equals(ruleId) || "LM008".equals(ruleId)) {
                dist.put("MRP Declarations", dist.get("MRP Declarations") + 1);
            } else if ("LM002".equals(ruleId) || "LM007".equals(ruleId)) {
                dist.put("Net Quantity", dist.get("Net Quantity") + 1);
            } else if ("LM001".equals(ruleId)) {
                dist.put("Manufacturer / Packer", dist.get("Manufacturer / Packer") + 1);
            } else if ("LM004".equals(ruleId)) {
                dist.put("Country of Origin", dist.get("Country of Origin") + 1);
            } else if ("LM005".equals(ruleId)) {
                dist.put("Consumer Care", dist.get("Consumer Care") + 1);
            } else if ("LM006".equals(ruleId)) {
                dist.put("Standard Unit of Measure", dist.get("Standard Unit of Measure") + 1);
            } else if ("LM009".equals(ruleId)) {
                dist.put("Date of Mfg / Expiry", dist.get("Date of Mfg / Expiry") + 1);
            } else if ("LM010".equals(ruleId)) {
                dist.put("E-Commerce Marketplace Parity", dist.get("E-Commerce Marketplace Parity") + 1);
            } else {
                dist.put("MRP Declarations", dist.get("MRP Declarations") + 1);
            }
        }
        dto.setViolationDistribution(dist);

        // Scan Jobs
        dto.setTotalScans(scanJobRepository.count());
        long active = 0;
        for (ScanJob j : scanJobRepository.findAll()) {
            if (j.getStatus() == ScanJobStatus.PROCESSING ||
                j.getStatus() == ScanJobStatus.OCR_PROCESSING ||
                j.getStatus() == ScanJobStatus.VALIDATING) {
                active++;
            }
        }
        dto.setActiveScans(active);

        // Category Risks
        dto.setCategoryRisks(buildCategoryRisks(products));

        // Seller Risks
        dto.setSellerRisks(buildSellerRisks(products));

        // Compliance Trends (7-day window based on real compliant / total scanned calculation)
        dto.setComplianceTrends(buildTrends(products, allViolations, dto.getOverallComplianceRate()));

        return dto;
    }

    private List<DashboardSummaryDto.CategoryRiskDto> buildCategoryRisks(List<Product> products) {
        Map<Long, List<Product>> byCat = new HashMap<>();
        for (Product p : products) {
            Long catId = p.getCategoryId() != null ? p.getCategoryId() : 1L;
            byCat.computeIfAbsent(catId, k -> new ArrayList<>()).add(p);
        }

        List<DashboardSummaryDto.CategoryRiskDto> list = new ArrayList<>();
        Map<Long, String> catNames = new HashMap<>();
        categoryRepository.findAll().forEach(c -> catNames.put(c.getId(), c.getName()));

        for (Map.Entry<Long, List<Product>> entry : byCat.entrySet()) {
            String name = catNames.getOrDefault(entry.getKey(), "Category " + entry.getKey());
            long total = entry.getValue().size();
            long nc = entry.getValue().stream()
                    .filter(p -> "POTENTIAL_NON_COMPLIANT".equalsIgnoreCase(p.getStatus()))
                    .count();
            double compRate = total > 0 ? Math.round(((double)(total - nc) / total) * 1000.0) / 10.0 : 100.0;
            String risk = compRate < 60 ? "CRITICAL" : (compRate < 80 ? "HIGH" : (compRate < 90 ? "MEDIUM" : "LOW"));
            list.add(new DashboardSummaryDto.CategoryRiskDto(name, total, nc, compRate, risk));
        }

        list.sort(Comparator.comparingDouble(DashboardSummaryDto.CategoryRiskDto::getComplianceRate));
        return list;
    }

    private List<DashboardSummaryDto.SellerRiskDto> buildSellerRisks(List<Product> products) {
        Map<Long, List<Product>> bySeller = new HashMap<>();
        for (Product p : products) {
            Long sId = p.getSellerId() != null ? p.getSellerId() : 1L;
            bySeller.computeIfAbsent(sId, k -> new ArrayList<>()).add(p);
        }

        Map<Long, Seller> sellerMap = new HashMap<>();
        sellerRepository.findAll().forEach(s -> sellerMap.put(s.getId(), s));

        List<DashboardSummaryDto.SellerRiskDto> list = new ArrayList<>();
        for (Map.Entry<Long, List<Product>> entry : bySeller.entrySet()) {
            Seller seller = sellerMap.get(entry.getKey());
            String sName = seller != null ? seller.getName() : "Seller " + entry.getKey();
            String platform = seller != null && seller.getLocation() != null ? seller.getLocation() : "Regional";


            long total = entry.getValue().size();
            long nonComp = entry.getValue().stream()
                    .filter(p -> "POTENTIAL_NON_COMPLIANT".equalsIgnoreCase(p.getStatus()))
                    .count();
            double rate = total > 0 ? Math.round(((double)(total - nonComp) / total) * 1000.0) / 10.0 : 100.0;
            String risk = rate < 50 ? "CRITICAL" : (rate < 75 ? "HIGH" : (rate < 90 ? "MEDIUM" : "LOW"));

            list.add(new DashboardSummaryDto.SellerRiskDto(sName, platform, total, nonComp, rate, risk));
        }

        list.sort(Comparator.comparingDouble(DashboardSummaryDto.SellerRiskDto::getComplianceRate));
        return list;
    }

    private List<DashboardSummaryDto.TrendDataPointDto> buildTrends(List<Product> products, List<Violation> violations, double overallComplianceRate) {
        List<DashboardSummaryDto.TrendDataPointDto> trends = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM");

        long totalScanned = products.size();
        long totalCompliant = products.stream()
                .filter(p -> "PASSED_AUTOMATED_CHECKS".equalsIgnoreCase(p.getStatus()))
                .count();
        double realComplianceRate = totalScanned > 0
                ? Math.round(((double) totalCompliant / totalScanned) * 1000.0) / 10.0
                : 100.0;

        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);

            List<Product> prodsUpToDay = products.stream()
                    .filter(p -> p.getCreatedAt() != null && !p.getCreatedAt().toLocalDate().isAfter(d))
                    .toList();

            double dayRate;
            long dayViolations;

            if (!prodsUpToDay.isEmpty()) {
                long compliantCount = prodsUpToDay.stream()
                        .filter(p -> "PASSED_AUTOMATED_CHECKS".equalsIgnoreCase(p.getStatus()))
                        .count();
                dayRate = Math.round(((double) compliantCount / prodsUpToDay.size()) * 1000.0) / 10.0;
                dayViolations = violations.stream()
                        .filter(v -> v.getTimestamp() != null && !v.getTimestamp().toLocalDate().isAfter(d))
                        .count();
            } else {
                dayRate = realComplianceRate;
                dayViolations = violations.size();
            }

            trends.add(new DashboardSummaryDto.TrendDataPointDto(d.format(fmt), dayRate, dayViolations));
        }
        return trends;
    }
}
