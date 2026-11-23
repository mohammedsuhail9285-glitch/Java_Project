package com.example.lmchecker.service;

import com.example.lmchecker.dto.ViolationDto;
import com.example.lmchecker.entity.Product;
import com.example.lmchecker.entity.Seller;
import com.example.lmchecker.exception.ResourceNotFoundException;
import com.example.lmchecker.repository.ProductRepository;
import com.example.lmchecker.repository.SellerRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final ViolationService violationService;

    public ReportService(ProductRepository productRepository,
                         SellerRepository sellerRepository,
                         ViolationService violationService) {
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
        this.violationService = violationService;
    }

    public byte[] generateProductPdfReport(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        List<ViolationDto> violations = violationService.getViolationsByProduct(productId);
        Seller seller = product.getSellerId() != null ? sellerRepository.findById(product.getSellerId()).orElse(null) : null;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
            com.lowagie.text.Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(50, 70, 90));
            com.lowagie.text.Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
            com.lowagie.text.Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
            com.lowagie.text.Font disclaimerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, new Color(130, 40, 40));

            // Header Banner
            Paragraph header = new Paragraph("GOVERNMENT OF INDIA — DEPARTMENT OF CONSUMER AFFAIRS", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph sub = new Paragraph("LEGAL METROLOGY (PACKAGED COMMODITIES) RULES, 2011\nAUTOMATED SCREENING & DECISION SUPPORT REPORT", subTitleFont);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(10);
            document.add(sub);

            // Mandatory Statutory Disclaimer Callout
            PdfPTable disclaimerTable = new PdfPTable(1);
            disclaimerTable.setWidthPercentage(100);
            PdfPCell discCell = new PdfPCell();
            discCell.setBackgroundColor(new Color(254, 242, 242));
            discCell.setBorderColor(new Color(239, 68, 68));
            discCell.setPadding(8);
            Paragraph discPara = new Paragraph(
                    "STATUTORY REGULATORY DISCLAIMER: This document is produced by an automated regulatory decision-support system. " +
                    "All findings represent 'Potential Non-Compliance' or 'Requires Review' and DO NOT constitute a final legal verdict " +
                    "or conclusive determination under the Legal Metrology Act, 2009. Human verification and statutory show-cause procedures " +
                    "must be followed prior to regulatory enforcement.",
                    disclaimerFont
            );
            discCell.addElement(discPara);
            disclaimerTable.addCell(discCell);
            disclaimerTable.setSpacingAfter(15);
            document.add(disclaimerTable);

            // Product Details Table
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1, 2});

            addTableRow(infoTable, "Product Name:", product.getName(), boldFont, bodyFont);
            addTableRow(infoTable, "Product ID / SKU:", "PRD-" + product.getId(), boldFont, bodyFont);
            addTableRow(infoTable, "Declared Label MRP:", product.getDeclaredMrp() != null ? "₹" + product.getDeclaredMrp() : "Not Declared", boldFont, bodyFont);
            addTableRow(infoTable, "Marketplace Listing Price:", product.getListingPrice() != null ? "₹" + product.getListingPrice() : "Not Listed", boldFont, bodyFont);
            addTableRow(infoTable, "Seller / Vendor:", seller != null ? seller.getName() + " (" + (seller.getLocation() != null ? seller.getLocation() : "India") + ")" : "Unknown", boldFont, bodyFont);
            addTableRow(infoTable, "Compliance Status:", product.getStatus(), boldFont, bodyFont);
            addTableRow(infoTable, "Inspection Timestamp:", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), boldFont, bodyFont);

            infoTable.setSpacingAfter(15);
            document.add(infoTable);

            // Potential Violations Section
            Paragraph secTitle = new Paragraph("OBSERVED POTENTIAL STATUTORY DEVIATIONS (" + violations.size() + ")", subTitleFont);
            secTitle.setSpacingAfter(8);
            document.add(secTitle);

            if (violations.isEmpty()) {
                Paragraph noVio = new Paragraph("Passed all automated verification checks. No prima facie statutory discrepancies detected.", bodyFont);
                document.add(noVio);
            } else {
                PdfPTable vioTable = new PdfPTable(4);
                vioTable.setWidthPercentage(100);
                vioTable.setWidths(new float[]{1.5f, 1.5f, 3.5f, 3.5f});

                addTableHeader(vioTable, "Rule ID", boldFont);
                addTableHeader(vioTable, "Severity", boldFont);
                addTableHeader(vioTable, "Detected Label Value", boldFont);
                addTableHeader(vioTable, "Statutory Requirement", boldFont);

                for (ViolationDto v : violations) {
                    vioTable.addCell(new Phrase(v.getRuleId() != null ? v.getRuleId() : "LM-GEN", bodyFont));
                    vioTable.addCell(new Phrase(v.getSeverity() != null ? v.getSeverity().name() : "MAJOR", bodyFont));
                    vioTable.addCell(new Phrase(v.getDetectedValue() != null ? v.getDetectedValue() : "Absent", bodyFont));
                    vioTable.addCell(new Phrase(v.getExpectedCondition() != null ? v.getExpectedCondition() : "Rule 6 Compliance", bodyFont));
                }
                document.add(vioTable);
            }

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report: " + e.getMessage(), e);
        }
    }

    public byte[] generateExcelReport() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<Product> products = productRepository.findAll();
            org.springframework.data.domain.Page<ViolationDto> violationsPage =
                    violationService.getViolations(null, null, null, null, org.springframework.data.domain.PageRequest.of(0, 1000));
            List<ViolationDto> violations = violationsPage.getContent();

            // Sheet 1: Potential Violations
            Sheet sheet1 = workbook.createSheet("Potential Violations");
            org.apache.poi.ss.usermodel.Row headerRow = sheet1.createRow(0);
            String[] headers = {"Violation ID", "Product ID", "Product Name", "Rule ID", "Rule Name", "Severity", "Detected Value", "Expected Condition", "Status", "OCR Confidence %"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (ViolationDto v : violations) {
                org.apache.poi.ss.usermodel.Row r = sheet1.createRow(rowIdx++);
                r.createCell(0).setCellValue(v.getViolationId() != null ? v.getViolationId() : "");
                r.createCell(1).setCellValue(v.getProductId() != null ? String.valueOf(v.getProductId()) : "");
                r.createCell(2).setCellValue(v.getProductName() != null ? v.getProductName() : "");
                r.createCell(3).setCellValue(v.getRuleId() != null ? v.getRuleId() : "");
                r.createCell(4).setCellValue(v.getRuleName() != null ? v.getRuleName() : "");
                r.createCell(5).setCellValue(v.getSeverity() != null ? v.getSeverity().name() : "");
                r.createCell(6).setCellValue(v.getDetectedValue() != null ? v.getDetectedValue() : "");
                r.createCell(7).setCellValue(v.getExpectedCondition() != null ? v.getExpectedCondition() : "");
                r.createCell(8).setCellValue(v.getStatus() != null ? v.getStatus().name() : "");
                r.createCell(9).setCellValue(v.getOcrConfidence() != null ? v.getOcrConfidence() : 0.0);
            }

            // Sheet 2: Products
            Sheet sheet2 = workbook.createSheet("Product Catalog");
            org.apache.poi.ss.usermodel.Row pHeader = sheet2.createRow(0);
            String[] pHeaders = {"ID", "Product Name", "Declared MRP", "Listing Price", "Country of Origin", "Status"};
            for (int i = 0; i < pHeaders.length; i++) {
                pHeader.createCell(i).setCellValue(pHeaders[i]);
            }

            int pIdx = 1;
            for (Product p : products) {
                org.apache.poi.ss.usermodel.Row r = sheet2.createRow(pIdx++);
                r.createCell(0).setCellValue(p.getId());
                r.createCell(1).setCellValue(p.getName() != null ? p.getName() : "");
                r.createCell(2).setCellValue(p.getDeclaredMrp() != null ? p.getDeclaredMrp() : 0.0);
                r.createCell(3).setCellValue(p.getListingPrice() != null ? p.getListingPrice() : 0.0);
                r.createCell(4).setCellValue(p.getCountryOfOrigin() != null ? p.getCountryOfOrigin() : "");
                r.createCell(5).setCellValue(p.getStatus() != null ? p.getStatus() : "");
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel report: " + e.getMessage(), e);
        }
    }

    public byte[] generateCsvReport() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Violation ID", "Product ID", "Product Name", "Rule ID", "Severity", "Detected Value", "Status"))) {

            org.springframework.data.domain.Page<ViolationDto> page =
                    violationService.getViolations(null, null, null, null, org.springframework.data.domain.PageRequest.of(0, 1000));

            for (ViolationDto v : page.getContent()) {
                csvPrinter.printRecord(
                        v.getViolationId(),
                        v.getProductId(),
                        v.getProductName(),
                        v.getRuleId(),
                        v.getSeverity() != null ? v.getSeverity().name() : "",
                        v.getDetectedValue(),
                        v.getStatus() != null ? v.getStatus().name() : ""
                );
            }
            csvPrinter.flush();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate CSV report: " + e.getMessage(), e);
        }
    }

    private void addTableRow(PdfPTable table, String label, String value, com.lowagie.text.Font labelFont, com.lowagie.text.Font valFont) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, labelFont));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setPadding(3);
        PdfPCell c2 = new PdfPCell(new Phrase(value != null ? value : "-", valFont));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setPadding(3);
        table.addCell(c1);
        table.addCell(c2);
    }

    private void addTableHeader(PdfPTable table, String header, com.lowagie.text.Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(header, font));
        cell.setBackgroundColor(new Color(241, 245, 249));
        cell.setPadding(5);
        table.addCell(cell);
    }
}
