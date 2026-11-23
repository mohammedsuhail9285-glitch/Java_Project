package com.example.lmchecker.service;

import com.example.lmchecker.entity.FreightMarketData;
import com.example.lmchecker.entity.MaritimePort;
import com.example.lmchecker.entity.ProcurementPlan;
import com.example.lmchecker.entity.TradeRoute;
import com.example.lmchecker.repository.FreightMarketDataRepository;
import com.example.lmchecker.repository.MaritimePortRepository;
import com.example.lmchecker.repository.ProcurementPlanRepository;
import com.example.lmchecker.repository.TradeRouteRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FreightReportService {

    private static final Logger log = LoggerFactory.getLogger(FreightReportService.class);

    private final ProcurementPlanRepository procurementPlanRepository;
    private final MaritimePortRepository portRepository;
    private final TradeRouteRepository tradeRouteRepository;
    private final FreightMarketDataRepository marketDataRepository;

    public FreightReportService(ProcurementPlanRepository procurementPlanRepository,
                                MaritimePortRepository portRepository,
                                TradeRouteRepository tradeRouteRepository,
                                FreightMarketDataRepository marketDataRepository) {
        this.procurementPlanRepository = procurementPlanRepository;
        this.portRepository = portRepository;
        this.tradeRouteRepository = tradeRouteRepository;
        this.marketDataRepository = marketDataRepository;
    }

    public byte[] generatePlanPdfDossier(Long planId) {
        ProcurementPlan plan = procurementPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Procurement Plan not found with id: " + planId));

        FreightMarketData market = marketDataRepository.findTopByOrderByRecordDateDesc().orElse(null);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 17, new Color(15, 23, 42));
            com.lowagie.text.Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(71, 85, 105));
            com.lowagie.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(30, 41, 59));
            com.lowagie.text.Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(15, 23, 42));

            // Header Banner
            Paragraph header = new Paragraph("INTELLIGENT FREIGHT FORECASTING & CHARTERING DOSSIER", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph sub = new Paragraph("Ministry of Steel, Govt of India • SIH26006 Decision Support System\nGenerated: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")), subtitleFont);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(12);
            document.add(sub);

            // 1. Plan Summary Table
            document.add(new Paragraph("1. PROCUREMENT ORDER SPECIFICATIONS", sectionFont));
            PdfPTable summaryTable = new PdfPTable(4);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingBefore(4);
            summaryTable.setSpacingAfter(10);

            addCell(summaryTable, "Plan Reference", true);
            addCell(summaryTable, plan.getPlanReference(), false);
            addCell(summaryTable, "Commodity", true);
            addCell(summaryTable, plan.getCommodity(), false);

            addCell(summaryTable, "Cargo Quantity", true);
            addCell(summaryTable, String.format("%,.0f MT", plan.getQuantityTons()), false);
            addCell(summaryTable, "Origin Country", true);
            addCell(summaryTable, plan.getOriginCountry(), false);

            addCell(summaryTable, "Destination Port", true);
            addCell(summaryTable, plan.getDestinationPort() != null ? plan.getDestinationPort().getPortName() : "N/A", false);
            addCell(summaryTable, "Recommended Vessel", true);
            addCell(summaryTable, plan.getRecommendedVesselClass(), false);

            addCell(summaryTable, "Contract Type", true);
            addCell(summaryTable, plan.getContractType() != null ? plan.getContractType() : "SPOT", false);
            addCell(summaryTable, "Target Laycan", true);
            addCell(summaryTable, plan.getTargetLaycanStart() + " to " + plan.getTargetLaycanEnd(), false);

            document.add(summaryTable);

            // 2. Charter Decision & Landed Cost Breakdown
            document.add(new Paragraph("2. CHARTERING DETERMINATION & LANDED COST WATERFALL", sectionFont));
            PdfPTable costTable = new PdfPTable(2);
            costTable.setWidthPercentage(100);
            costTable.setSpacingBefore(4);
            costTable.setSpacingAfter(10);

            addCell(costTable, "CHARTER DECISION", true);
            addCell(costTable, plan.getCharterDecision(), false);

            addCell(costTable, "FOB Cargo Base Price", true);
            addCell(costTable, String.format("$%.2f / MT", plan.getFobCargoPriceUsdTon()), false);

            addCell(costTable, "Predicted Ocean Freight Rate", true);
            addCell(costTable, String.format("$%.2f / MT", plan.getPredictedFreightUsdTon()), false);

            addCell(costTable, "Bunker Fuel Adjustment (BAF)", true);
            addCell(costTable, String.format("$%.2f / MT", plan.getBunkerAdjustmentUsdTon()), false);

            addCell(costTable, "Port Tariff & Stevedoring", true);
            addCell(costTable, String.format("$%.2f / MT", plan.getPortChargesUsdTon()), false);

            addCell(costTable, "Demurrage / Idle Time Risk", true);
            addCell(costTable, String.format("$%.2f / MT (Risk Level: %s)", plan.getDemurrageRiskUsdTon(), plan.getDemurrageRiskLevel()), false);

            if (Boolean.TRUE.equals(plan.getLighterageRequired())) {
                addCell(costTable, "Sagar/Sandheads Lighterage Tariff", true);
                addCell(costTable, String.format("$%.2f / MT (%,.0f MT discharged at %s)",
                        plan.getLighterageCostUsdTon(), plan.getLighteredQuantityTons(), plan.getLighterageAnchorage()), false);
            }

            addCell(costTable, "TOTAL ESTIMATED LANDED COST (USD)", true);
            addCell(costTable, String.format("$%.2f / MT", plan.getTotalLandedCostUsdTon()), false);

            addCell(costTable, "TOTAL ESTIMATED LANDED COST (INR)", true);
            addCell(costTable, String.format("₹%,.2f / MT", plan.getTotalLandedCostInrTon()), false);

            addCell(costTable, "PROJECTED PROCUREMENT SAVINGS", true);
            addCell(costTable, String.format("₹%,.2f", plan.getEstimatedCostSavingsInr()), false);

            document.add(costTable);

            // 3. Operational & Nautical Details
            if (plan.getAlternativePortRecommendation() != null || Boolean.TRUE.equals(plan.getLighterageRequired())) {
                document.add(new Paragraph("3. NAUTICAL FEASIBILITY & ALTERNATIVE PORT DISPATCH", sectionFont));
                PdfPTable nautTable = new PdfPTable(2);
                nautTable.setWidthPercentage(100);
                nautTable.setSpacingBefore(4);
                nautTable.setSpacingAfter(10);

                if (Boolean.TRUE.equals(plan.getLighterageRequired())) {
                    addCell(nautTable, "Lighterage Protocol", true);
                    addCell(nautTable, "MANDATORY: Channel draft restricted to 11.0m. Deep-draft daughter barge transshipment at " + plan.getLighterageAnchorage(), false);
                }

                if (plan.getAlternativePortRecommendation() != null) {
                    addCell(nautTable, "Alternative Port Diversion", true);
                    addCell(nautTable, plan.getAlternativePortRecommendation(), false);
                }

                document.add(nautTable);
            }

            // 4. Decision Rationale
            document.add(new Paragraph("4. AI DECISION ENGINE RATIONALE & MARKET INTELLIGENCE", sectionFont));
            Paragraph rationale = new Paragraph(plan.getDecisionRationale(), cellFont);
            rationale.setSpacingBefore(4);
            rationale.setSpacingAfter(12);
            document.add(rationale);

            // Footer / Disclaimer
            Paragraph disclaimer = new Paragraph("Statutory Notice: This procurement dossier is generated by the SIH26006 Intelligent Freight Forecasting Model utilizing Baltic Exchange dry-bulk predictive neural regression, port navigational constraints, and demurrage risk calculations.", subtitleFont);
            disclaimer.setAlignment(Element.ALIGN_CENTER);
            document.add(disclaimer);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating freight PDF dossier: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate Freight PDF Dossier: " + e.getMessage());
        }
    }

    public byte[] generateFreightExcelWorkbook() {
        List<ProcurementPlan> plans = procurementPlanRepository.findAllByOrderByCreatedAtDesc();
        List<MaritimePort> ports = portRepository.findAll();
        List<TradeRoute> routes = tradeRouteRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet1 = workbook.createSheet("Procurement Plans");
            Sheet sheet2 = workbook.createSheet("East Coast Ports");
            Sheet sheet3 = workbook.createSheet("Trade Routes");

            // Sheet 1: Plans
            String[] planHeaders = {
                    "Reference", "Commodity", "Quantity (MT)", "Origin", "Port", "Vessel",
                    "Contract", "Decision", "Freight $/MT", "Lighterage $/MT", "Demurrage $/MT",
                    "Landed INR/MT", "Savings INR", "Risk Level", "Alternative Port"
            };
            Row r0 = sheet1.createRow(0);
            for (int i = 0; i < planHeaders.length; i++) {
                r0.createCell(i).setCellValue(planHeaders[i]);
            }
            int rowIdx = 1;
            for (ProcurementPlan p : plans) {
                Row r = sheet1.createRow(rowIdx++);
                r.createCell(0).setCellValue(p.getPlanReference());
                r.createCell(1).setCellValue(p.getCommodity());
                r.createCell(2).setCellValue(p.getQuantityTons());
                r.createCell(3).setCellValue(p.getOriginCountry());
                r.createCell(4).setCellValue(p.getDestinationPort() != null ? p.getDestinationPort().getPortCode() : "");
                r.createCell(5).setCellValue(p.getRecommendedVesselClass());
                r.createCell(6).setCellValue(p.getContractType() != null ? p.getContractType() : "SPOT");
                r.createCell(7).setCellValue(p.getCharterDecision());
                r.createCell(8).setCellValue(p.getPredictedFreightUsdTon());
                r.createCell(9).setCellValue(p.getLighterageCostUsdTon() != null ? p.getLighterageCostUsdTon() : 0.0);
                r.createCell(10).setCellValue(p.getDemurrageRiskUsdTon() != null ? p.getDemurrageRiskUsdTon() : 0.0);
                r.createCell(11).setCellValue(p.getTotalLandedCostInrTon());
                r.createCell(12).setCellValue(p.getEstimatedCostSavingsInr());
                r.createCell(13).setCellValue(p.getDemurrageRiskLevel() != null ? p.getDemurrageRiskLevel() : "LOW");
                r.createCell(14).setCellValue(p.getAlternativePortRecommendation() != null ? p.getAlternativePortRecommendation() : "N/A");
            }

            // Sheet 2: Ports
            String[] portHeaders = {
                    "Port Code", "Port Name", "Max Draft (m)", "Max LOA (m)", "Max Beam (m)",
                    "Max DWT", "Discharge Rate (TPD)", "Wait Days", "Congestion Index", "Lighterage Supported"
            };
            Row p0 = sheet2.createRow(0);
            for (int i = 0; i < portHeaders.length; i++) {
                p0.createCell(i).setCellValue(portHeaders[i]);
            }
            rowIdx = 1;
            for (MaritimePort pt : ports) {
                Row r = sheet2.createRow(rowIdx++);
                r.createCell(0).setCellValue(pt.getPortCode());
                r.createCell(1).setCellValue(pt.getPortName());
                r.createCell(2).setCellValue(pt.getMaxDraftMeters());
                r.createCell(3).setCellValue(pt.getMaxLoaMeters() != null ? pt.getMaxLoaMeters() : 280.0);
                r.createCell(4).setCellValue(pt.getMaxBeamMeters() != null ? pt.getMaxBeamMeters() : 45.0);
                r.createCell(5).setCellValue(pt.getMaxDwt());
                r.createCell(6).setCellValue(pt.getDischargeRateTpd());
                r.createCell(7).setCellValue(pt.getAverageBerthWaitDays());
                r.createCell(8).setCellValue(pt.getCongestionIndex());
                r.createCell(9).setCellValue(Boolean.TRUE.equals(pt.getLighterageSupported()) ? "YES (" + pt.getLighterageZone() + ")" : "NO");
            }

            // Sheet 3: Routes
            String[] routeHeaders = {"Route Code", "Origin Port", "Origin Country", "Destination Port", "Commodity", "Distance (NM)", "Benchmark $/MT"};
            Row rt0 = sheet3.createRow(0);
            for (int i = 0; i < routeHeaders.length; i++) {
                rt0.createCell(i).setCellValue(routeHeaders[i]);
            }
            rowIdx = 1;
            for (TradeRoute rt : routes) {
                Row r = sheet3.createRow(rowIdx++);
                r.createCell(0).setCellValue(rt.getRouteCode());
                r.createCell(1).setCellValue(rt.getOriginPort());
                r.createCell(2).setCellValue(rt.getOriginCountry());
                r.createCell(3).setCellValue(rt.getDestinationPort() != null ? rt.getDestinationPort().getPortName() : "");
                r.createCell(4).setCellValue(rt.getCommodity());
                r.createCell(5).setCellValue(rt.getDistanceNauticalMiles());
                r.createCell(6).setCellValue(rt.getBenchmarkRateUsdPerTon());
            }

            for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
                Sheet curSheet = workbook.getSheetAt(s);
                for (int c = 0; c < 15; c++) {
                    curSheet.autoSizeColumn(c);
                }
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating freight Excel workbook: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate Freight Excel Workbook: " + e.getMessage());
        }
    }

    public byte[] generateFreightCsvReport() {
        List<ProcurementPlan> plans = procurementPlanRepository.findAllByOrderByCreatedAtDesc();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader(
                             "PlanReference", "Commodity", "QuantityTons", "OriginCountry",
                             "DestinationPortCode", "DestinationPortName", "RecommendedVessel",
                             "ContractType", "CharterDecision", "CoaDiscountPct", "FobCargoPriceUsdTon",
                             "PredictedFreightUsdTon", "BunkerAdjustmentUsdTon", "PortChargesUsdTon",
                             "DemurrageRiskUsdTon", "LighterageRequired", "LighterageAnchorage",
                             "LighteredQuantityTons", "LighterageCostUsdTon", "TotalLandedCostUsdTon",
                             "TotalLandedCostInrTon", "EstimatedCostSavingsInr", "DemurrageRiskLevel",
                             "AlternativePortRecommendation", "VolatilityAlertLevel", "Status", "CreatedAt"
                     ).build())) {

            for (ProcurementPlan p : plans) {
                csvPrinter.printRecord(
                        p.getPlanReference(),
                        p.getCommodity(),
                        p.getQuantityTons(),
                        p.getOriginCountry(),
                        p.getDestinationPort() != null ? p.getDestinationPort().getPortCode() : "",
                        p.getDestinationPort() != null ? p.getDestinationPort().getPortName() : "",
                        p.getRecommendedVesselClass(),
                        p.getContractType() != null ? p.getContractType() : "SPOT",
                        p.getCharterDecision(),
                        p.getCoaDiscountPct() != null ? p.getCoaDiscountPct() : 0.0,
                        p.getFobCargoPriceUsdTon(),
                        p.getPredictedFreightUsdTon(),
                        p.getBunkerAdjustmentUsdTon(),
                        p.getPortChargesUsdTon(),
                        p.getDemurrageRiskUsdTon(),
                        p.getLighterageRequired() != null && p.getLighterageRequired() ? "YES" : "NO",
                        p.getLighterageAnchorage() != null ? p.getLighterageAnchorage() : "N/A",
                        p.getLighteredQuantityTons() != null ? p.getLighteredQuantityTons() : 0.0,
                        p.getLighterageCostUsdTon() != null ? p.getLighterageCostUsdTon() : 0.0,
                        p.getTotalLandedCostUsdTon(),
                        p.getTotalLandedCostInrTon(),
                        p.getEstimatedCostSavingsInr(),
                        p.getDemurrageRiskLevel() != null ? p.getDemurrageRiskLevel() : "LOW",
                        p.getAlternativePortRecommendation() != null ? p.getAlternativePortRecommendation() : "N/A",
                        p.getVolatilityAlertLevel() != null ? p.getVolatilityAlertLevel() : "NORMAL",
                        p.getStatus(),
                        p.getCreatedAt() != null ? p.getCreatedAt().toString() : ""
                );
            }

            csvPrinter.flush();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generating freight CSV report: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate Freight CSV Report: " + e.getMessage());
        }
    }

    private void addCell(PdfPTable table, String text, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(text, isHeader ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(30, 41, 59)) : FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(51, 65, 85))));
        cell.setPadding(5);
        cell.setBackgroundColor(isHeader ? new Color(241, 245, 249) : Color.WHITE);
        cell.setBorderColor(new Color(226, 232, 240));
        table.addCell(cell);
    }
}
