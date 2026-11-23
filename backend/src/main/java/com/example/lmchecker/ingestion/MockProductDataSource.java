package com.example.lmchecker.ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MockProductDataSource implements ProductDataSource {

    private static final Logger log = LoggerFactory.getLogger(MockProductDataSource.class);

    private final List<IngestedProductData> mockCatalog = new ArrayList<>();

    public MockProductDataSource() {
        initMockCatalog();
    }

    private void initMockCatalog() {
        mockCatalog.add(new IngestedProductData(
            "Swarna Shakti Organic Whole Wheat Atta 5kg",
            "Swarna Shakti Foods",
            "RetailBharat Supermart LLP",
            "Packaged Food & Staples",
            320.00,
            299.00,
            "100% stone ground whole wheat flour with natural dietary fiber.",
            "India",
            "https://retailbharat.in/p/swarna-atta-5kg",
            "storage/uploads/mock_label_atta_5kg.png",
            Map.of("netQuantity", "5 kg", "mrp", "320.00", "manufacturer", "Swarna Shakti Consumer Ltd, Pune", "origin", "India")
        ));

        mockCatalog.add(new IngestedProductData(
            "Vedika Purifying Neem Face Wash 150ml",
            "Vedika Herbal Care",
            "Apex Global Traders",
            "Personal Care & Cosmetics",
            185.00,
            160.00,
            "Gentle herbal foaming face wash with neem and tea tree.",
            "India",
            "https://apexglobalkart.com/p/vedika-facewash-150",
            "storage/uploads/mock_label_facewash_150ml.png",
            Map.of("netQuantity", "150 ml.", "mrp", "185.00", "manufacturer", "Vedika Ayurveda, Haridwar", "origin", "India")
        ));

        mockCatalog.add(new IngestedProductData(
            "Apex Glow Hydrating Sheet Mask 25g Pack of 5",
            "Apex Glow Cosmetics",
            "Western Imports & Marketing",
            "Personal Care & Cosmetics",
            650.00,
            499.00,
            "Imported Korean-style hydration facial sheet mask.",
            "China",
            "https://westernimports.biz/p/apex-sheetmask-pack5",
            "storage/uploads/mock_label_sheetmask_25g.png",
            Map.of("netQuantity", "25 g", "mrp", "650.00", "manufacturer", "Apex Global Guangzhou", "origin", "China")
        ));

        mockCatalog.add(new IngestedProductData(
            "NutriBoost Plant Protein Powder Belgian Chocolate 1kg",
            "NutriBoost Global",
            "Apex Global Traders",
            "Health Supplements & Nutraceuticals",
            2499.00,
            2199.00,
            "25g pure plant isolate protein with digestive enzymes.",
            "India",
            "https://apexglobalkart.com/p/nutriboost-protein-1kg",
            "storage/uploads/mock_label_protein_1kg.png",
            Map.of("netQuantity", "1 kg", "mrp", "2499.00", "manufacturer", "NutriBoost Labs Mumbai", "origin", "India")
        ));

        mockCatalog.add(new IngestedProductData(
            "VoltMax 65W GaN Fast Charger Dual Port",
            "VoltMax Electronics",
            "Apex Global Traders",
            "Electronics & Appliances",
            1899.00,
            1499.00,
            "Ultra-compact GaN fast wall charger for laptop and mobile.",
            "China",
            "https://apexglobalkart.com/p/voltmax-gan-charger",
            "storage/uploads/mock_label_charger_65w.png",
            Map.of("netQuantity", "1 Unit", "mrp", "1899.00", "importer", "Apex Global Traders New Delhi", "origin", "China")
        ));

        // Test scenario: Overcharge / Price Mismatch (Listing price ₹350 exceeds package label MRP ₹299)
        mockCatalog.add(new IngestedProductData(
            "Himalayan Organic Multiflora Honey 500g",
            "Himalayan Nectar",
            "RetailBharat Supermart LLP",
            "Packaged Food & Staples",
            299.00,
            349.00, // Listing price > MRP (Overcharge violation LM008)
            "100% pure raw unprocessed Himalayan forest honey.",
            "India",
            "https://retailbharat.in/p/himalayan-honey-overcharge",
            "storage/uploads/mock_label_atta_5kg.png",
            Map.of("netQuantity", "500 g", "mrp", "299.00", "manufacturer", "Himalayan Apiaries Ltd, Shimla", "origin", "India")
        ));

        // Test scenario: Missing MRP (No MRP declared on physical commodity label)
        mockCatalog.add(new IngestedProductData(
            "EcoSpark Herbal Laundry Detergent 1L",
            "EcoSpark Cleaners",
            "Western Imports & Marketing",
            "Home & Kitchen Cleaning",
            199.00,
            175.00,
            "Eco-friendly plant-based liquid detergent for delicates.",
            "India",
            "https://retailbharat.in/p/ecospark-missing-mrp",
            "storage/uploads/mock_label_missing_mrp.png",
            Map.of("netQuantity", "1 L", "manufacturer", "EcoSpark Home Solutions, Vadodara", "origin", "India")
        ));
    }

    @Override
    public String getSourceType() {
        return "SIMULATED_CRAWLER";
    }

    @Override
    public IngestedProductData fetchProductFromUrl(String url) {
        log.info("Simulating sandbox crawler fetch for URL: {}", url);
        String lower = url.toLowerCase();

        // 1. Direct keyword matching for test cases and products
        if (lower.contains("overcharge") || lower.contains("mismatch")) {
            return mockCatalog.stream().filter(p -> p.productUrl().contains("overcharge")).findFirst().orElse(mockCatalog.get(5));
        }
        if (lower.contains("missing-mrp") || lower.contains("no-mrp")) {
            return mockCatalog.stream().filter(p -> p.productUrl().contains("missing-mrp")).findFirst().orElse(mockCatalog.get(6));
        }
        if (lower.contains("atta") || lower.contains("wheat") || lower.contains("flour")) {
            return mockCatalog.get(0);
        }
        if (lower.contains("facewash") || lower.contains("vedika") || lower.contains("neem")) {
            return mockCatalog.get(1);
        }
        if (lower.contains("sheetmask") || lower.contains("mask") || lower.contains("apex")) {
            return mockCatalog.get(2);
        }
        if (lower.contains("protein") || lower.contains("nutriboost")) {
            return mockCatalog.get(3);
        }
        if (lower.contains("charger") || lower.contains("voltmax") || lower.contains("electronics")) {
            return mockCatalog.get(4);
        }

        // 2. Substring matching across names and brands
        for (IngestedProductData item : mockCatalog) {
            String b = item.brandName().toLowerCase();
            String n = item.name().toLowerCase();
            if (lower.contains(b.substring(0, Math.min(4, b.length()))) ||
                lower.contains(n.substring(0, Math.min(5, n.length())))) {
                return item;
            }
        }

        // 3. Fallback: If URL does not match any known mock products, fail gracefully with friendly message
        throw new com.example.lmchecker.exception.BadRequestException(
            "Could not fetch this listing, paste the listing text instead"
        );
    }

    @Override
    public List<IngestedProductData> fetchSimulatedCrawlBatch(int count) {
        int targetCount = Math.min(count, mockCatalog.size());
        return new ArrayList<>(mockCatalog.subList(0, targetCount));
    }
}
