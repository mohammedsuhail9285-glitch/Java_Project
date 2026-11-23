-- Flyway Migration V2: Comprehensive Seed Data for RegTech Sentinel
-- Pre-populates Users, Categories, Sellers, Brands, 50+ Products, Rules LM001-LM010, Rule Versions, Violations & Audit Logs

-- 1. Seed Categories
INSERT INTO categories (id, name, parent_category, risk_weight, mandatory_fields) VALUES
(1, 'Packaged Food & Staples', 'Food & Beverages', 1.4, '["MRP","NET_QTY","MANUFACTURER","MFG_DATE","CONSUMER_CARE","COUNTRY_OF_ORIGIN"]'),
(2, 'Edible Oils & Ghee', 'Food & Beverages', 1.5, '["MRP","NET_QTY","MANUFACTURER","MFG_DATE","CONSUMER_CARE","COUNTRY_OF_ORIGIN"]'),
(3, 'Dairy & Breakfast Cereals', 'Food & Beverages', 1.3, '["MRP","NET_QTY","MANUFACTURER","MFG_DATE","CONSUMER_CARE","COUNTRY_OF_ORIGIN"]'),
(4, 'Personal Care & Cosmetics', 'Health & Beauty', 1.2, '["MRP","NET_QTY","MANUFACTURER","MFG_DATE","CONSUMER_CARE","COUNTRY_OF_ORIGIN"]'),
(5, 'Baby Care & Nutrition', 'Baby Products', 1.5, '["MRP","NET_QTY","MANUFACTURER","MFG_DATE","CONSUMER_CARE","COUNTRY_OF_ORIGIN"]'),
(6, 'Health Supplements & Nutraceuticals', 'Health & Beauty', 1.4, '["MRP","NET_QTY","MANUFACTURER","MFG_DATE","CONSUMER_CARE","COUNTRY_OF_ORIGIN"]'),
(7, 'Electronics & Appliances', 'Electronics', 1.1, '["MRP","NET_QTY","MANUFACTURER","CONSUMER_CARE","COUNTRY_OF_ORIGIN"]'),
(8, 'Home & Kitchen Cleaning', 'Household Goods', 1.0, '["MRP","NET_QTY","MANUFACTURER","CONSUMER_CARE","COUNTRY_OF_ORIGIN"]'),
(9, 'Pet Supplies & Food', 'Pet Supplies', 1.1, '["MRP","NET_QTY","MANUFACTURER","MFG_DATE","CONSUMER_CARE","COUNTRY_OF_ORIGIN"]'),
(10, 'Packaged Beverages & Juices', 'Food & Beverages', 1.3, '["MRP","NET_QTY","MANUFACTURER","MFG_DATE","CONSUMER_CARE","COUNTRY_OF_ORIGIN"]');

-- 2. Seed Sellers
INSERT INTO sellers (id, name, location, state_region, risk_score, contact_email, registration_number, total_products, violation_count) VALUES
(1, 'RetailBharat Supermart LLP', 'Bhiwandi Warehouse Cluster, Thane', 'Maharashtra', 68.5, 'compliance@retailbharat.in', 'GSTIN27AAACR1234F1Z1', 18, 5),
(2, 'Apex Global Traders', 'Okhla Industrial Area Phase III, New Delhi', 'Delhi', 78.0, 'legal@apexglobalkart.com', 'GSTIN07AABCA5678G1Z2', 14, 6),
(3, 'Heritage Agro Consumer Products', 'Whitefield Logistics Hub, Bengaluru', 'Karnataka', 15.0, 'info@heritageagro.org', 'GSTIN29AABCH9012H1Z3', 12, 0),
(4, 'QuickCommerce Direct Fulfillment', 'Ambattur Industrial Estate, Chennai', 'Tamil Nadu', 42.0, 'seller-desk@qcdirect.co', 'GSTIN33AACFQ3456J1Z4', 16, 2),
(5, 'Western Imports & Marketing', 'Naroda GIDC, Ahmedabad', 'Gujarat', 85.0, 'contact@westernimports.biz', 'GSTIN24AAACW7890K1Z5', 10, 5),
(6, 'Deccan Consumer Goods Pvt Ltd', 'Sanath Nagar, Hyderabad', 'Telangana', 22.0, 'support@deccanconsumer.com', 'GSTIN36AAACD2345L1Z6', 15, 1),
(7, 'Eastern Retail Network', 'Taratala Industrial Area, Kolkata', 'West Bengal', 54.0, 'grievance@easternretail.in', 'GSTIN19AAACE6789M1Z7', 11, 3),
(8, 'Northgate E-Commerce Ventures', 'Transport Nagar, Kanpur', 'Uttar Pradesh', 62.0, 'ops@northgateventures.in', 'GSTIN09AAACN0123N1Z8', 9, 3);

-- 3. Seed Brands
INSERT INTO brands (id, name, manufacturer_name, country_of_origin, total_products) VALUES
(1, 'Swarna Shakti Foods', 'Swarna Shakti Consumer Products Ltd, Pune', 'India', 8),
(2, 'PureDrops Organic Dairy', 'PureDrops Dairy Farms Pvt Ltd, Anand', 'India', 6),
(3, 'Vedika Herbal Care', 'Vedika Ayurveda & Naturals, Haridwar', 'India', 7),
(4, 'NutriBoost Global', 'NutriBoost Wellness Laboratories, Mumbai', 'India', 5),
(5, 'Apex Glow Cosmetics', 'Apex Global Formulations Inc, Guangzhou (Imported)', 'China', 4),
(6, 'Himalayan Spring Waters', 'Himalayan Natural Springs Ltd, Dehradun', 'India', 4),
(7, 'EcoSpark Cleaners', 'EcoSpark Home Clean Solutions, Vadodara', 'India', 6),
(8, 'LittleAngels Baby Care', 'LittleAngels Health & Hygiene Ltd, Manesar', 'India', 5),
(9, 'UrbanRoast Coffee', 'UrbanRoast Plantation & Processors, Chikmagalur', 'India', 4),
(10, 'Golden Harvest Spices', 'Golden Harvest Agri Spices Ltd, Guntur', 'India', 5),
(11, 'VoltMax Electronics', 'VoltMax Smart Tech Shenzhen Ltd (Imported by Apex)', 'China', 4),
(12, 'TrueNourish Pet Care', 'TrueNourish Animal Feeds Ltd, Chandigarh', 'India', 3);

-- 4. Seed Legal Metrology Rules (LM001 - LM010)
INSERT INTO rules (id, rule_id, rule_name, description, category, applicable_product_category, severity, validation_type, expected_field, enabled, version, effective_date, source, remediation_message) VALUES
(1, 'LM001', 'Name and Address of Manufacturer/Packer/Importer', 'Every package shall bear the name and complete address of the manufacturer, or where the manufacturer is not the packer, the name and address of the manufacturer and packer, or for imported commodities the name and address of the importer.', 'PACKAGED_COMMODITIES', 'ALL', 'CRITICAL', 'FIELD_PRESENCE', 'MANUFACTURER', true, 1, '2011-04-01', 'Rule 6(1)(a) of Legal Metrology (Packaged Commodities) Rules, 2011', 'Ensure clear display of complete postal address with PIN code and identity of manufacturer/packer/importer on both packaging and product listing.'),
(2, 'LM002', 'Declaration of Net Quantity', 'The net quantity in terms of the standard unit of weight or measure or in number shall be declared on the principal display panel in standard metric units.', 'PACKAGED_COMMODITIES', 'ALL', 'CRITICAL', 'FIELD_PRESENCE', 'NET_QTY', true, 1, '2011-04-01', 'Rule 6(1)(b) of Legal Metrology (Packaged Commodities) Rules, 2011', 'Declare exact net content in standard metric units (e.g., g, kg, ml, l) with appropriate font size on the principal display panel.'),
(3, 'LM003', 'Retail Sale Price (MRP) Inclusive of All Taxes', 'The maximum retail price (MRP) inclusive of all taxes must be clearly declared on the physical packaging and matching on e-commerce platforms.', 'PACKAGED_COMMODITIES', 'ALL', 'CRITICAL', 'FIELD_PRESENCE', 'MRP', true, 1, '2011-04-01', 'Rule 6(1)(d) of Legal Metrology (Packaged Commodities) Rules, 2011', 'Display "Maximum Retail Price / MRP Rs. / ₹ ... (inclusive of all taxes)" prominently.'),
(4, 'LM004', 'Country of Origin Declaration', 'For imported products or e-commerce listings, the country of manufacture or assembly must be clearly declared.', 'PACKAGED_COMMODITIES', 'ALL', 'MAJOR', 'FIELD_PRESENCE', 'COUNTRY_OF_ORIGIN', true, 1, '2017-06-23', 'Rule 6(1)(ac) and Rule 6(10) of Legal Metrology (Packaged Commodities) Rules, 2011', 'State the Country of Origin explicitly on the packaging and web product detail page.'),
(5, 'LM005', 'Consumer Care Details', 'The name, address, telephone number, and e-mail address of the person or office that may be contacted in case of consumer complaints must be stated.', 'PACKAGED_COMMODITIES', 'ALL', 'MAJOR', 'FIELD_PRESENCE', 'CONSUMER_CARE', true, 1, '2011-04-01', 'Rule 6(1)(g) of Legal Metrology (Packaged Commodities) Rules, 2011', 'Provide complete customer care contact details including dedicated phone number and email address.'),
(6, 'LM006', 'Standard Unit of Measurement Compliance', 'Symbols or units used for net quantity declarations must strictly conform to Schedule II. Non-standard abbreviations such as gms, gm, g., kgs, kgs., ml., ltr are non-compliant.', 'PACKAGED_COMMODITIES', 'ALL', 'MAJOR', 'UNIT_VALIDITY', 'UNIT', true, 1, '2011-04-01', 'Rule 11 and Schedule II of Legal Metrology (Packaged Commodities) Rules, 2011', 'Replace non-standard units (e.g., "gms", "ml.") with statutory metric symbols: "g", "kg", "ml", "l".'),
(7, 'LM007', 'Quantity Mismatch: Listing vs Physical Package', 'The quantity advertised on the e-commerce listing must match the net quantity declared on the physical commodity packaging.', 'PACKAGED_COMMODITIES', 'ALL', 'CRITICAL', 'CROSS_SOURCE_MISMATCH', 'NET_QTY', true, 1, '2011-04-01', 'Rule 6(10) read with Section 18 of Legal Metrology Act, 2009', 'Update e-commerce catalog to exactly match the net quantity declared on the physical product package.'),
(8, 'LM008', 'Retail Price Mismatch: Listing vs Physical Package', 'The maximum retail price (MRP) displayed on the digital platform must not exceed or diverge deceptively from the MRP marked on the physical package.', 'PACKAGED_COMMODITIES', 'ALL', 'CRITICAL', 'CROSS_SOURCE_MISMATCH', 'MRP', true, 1, '2011-04-01', 'Section 18 read with Section 36 of Legal Metrology Act, 2009', 'Ensure listed MRP does not exceed physical label MRP. Selling above marked MRP is an offence under Section 36.'),
(9, 'LM009', 'Month and Year of Manufacture/Packing/Import', 'The month and year in which the commodity is manufactured or packed or imported shall be declared on the package.', 'PACKAGED_COMMODITIES', 'ALL', 'MAJOR', 'DATE_PRESENCE', 'MFG_DATE', true, 1, '2011-04-01', 'Rule 6(1)(c) of Legal Metrology (Packaged Commodities) Rules, 2011', 'Declare Month and Year of manufacture or packing in words or numbers (e.g., "Mfg: 08/2026").'),
(10, 'LM010', 'E-Commerce Marketplace Declaration Parity', 'All mandatory declarations present on the package shall also be displayed on the digital and electronic network used for e-commerce transactions.', 'PACKAGED_COMMODITIES', 'ALL', 'MAJOR', 'LISTING_OMISSION', 'ALL_MANDATORY', true, 1, '2017-06-23', 'Rule 6(10) of Legal Metrology (Packaged Commodities) Rules, 2011', 'Ensure all mandatory label declarations (MRP, Net Qty, Origin, Packer) are mirrored on the product webpage.');

-- 5. Seed Rule Versions
INSERT INTO rule_versions (rule_id, version_number, config_snapshot, changed_by, change_reason, changed_at) VALUES
('LM001', 1, '{"ruleId":"LM001","severity":"CRITICAL","validationType":"FIELD_PRESENCE","expectedField":"MANUFACTURER","active":true}', 'system_init', 'Initial statutory baseline configuration', CURRENT_TIMESTAMP),
('LM002', 1, '{"ruleId":"LM002","severity":"CRITICAL","validationType":"FIELD_PRESENCE","expectedField":"NET_QTY","active":true}', 'system_init', 'Initial statutory baseline configuration', CURRENT_TIMESTAMP),
('LM003', 1, '{"ruleId":"LM003","severity":"CRITICAL","validationType":"FIELD_PRESENCE","expectedField":"MRP","active":true}', 'system_init', 'Initial statutory baseline configuration', CURRENT_TIMESTAMP),
('LM004', 1, '{"ruleId":"LM004","severity":"MAJOR","validationType":"FIELD_PRESENCE","expectedField":"COUNTRY_OF_ORIGIN","active":true}', 'system_init', 'Initial statutory baseline configuration', CURRENT_TIMESTAMP),
('LM005', 1, '{"ruleId":"LM005","severity":"MAJOR","validationType":"FIELD_PRESENCE","expectedField":"CONSUMER_CARE","active":true}', 'system_init', 'Initial statutory baseline configuration', CURRENT_TIMESTAMP),
('LM006', 1, '{"ruleId":"LM006","severity":"MAJOR","validationType":"UNIT_VALIDITY","expectedField":"UNIT","allowedUnits":["g","kg","ml","l","m","cm","mm","N","U"],"active":true}', 'system_init', 'Initial statutory baseline configuration', CURRENT_TIMESTAMP),
('LM007', 1, '{"ruleId":"LM007","severity":"CRITICAL","validationType":"CROSS_SOURCE_MISMATCH","field":"NET_QTY","tolerancePct":0.0,"active":true}', 'system_init', 'Initial statutory baseline configuration', CURRENT_TIMESTAMP),
('LM008', 1, '{"ruleId":"LM008","severity":"CRITICAL","validationType":"CROSS_SOURCE_MISMATCH","field":"MRP","tolerancePct":0.0,"active":true}', 'system_init', 'Initial statutory baseline configuration', CURRENT_TIMESTAMP),
('LM009', 1, '{"ruleId":"LM009","severity":"MAJOR","validationType":"DATE_PRESENCE","expectedField":"MFG_DATE","active":true}', 'system_init', 'Initial statutory baseline configuration', CURRENT_TIMESTAMP),
('LM010', 1, '{"ruleId":"LM010","severity":"MAJOR","validationType":"LISTING_OMISSION","expectedField":"ALL_MANDATORY","active":true}', 'system_init', 'Initial statutory baseline configuration', CURRENT_TIMESTAMP);

-- 6. Seed 50+ Synthetic Products across multiple categories and compliance states
INSERT INTO products (id, name, brand_id, seller_id, category_id, declared_mrp, listing_price, description, country_of_origin, product_url, ingestion_source, status, created_at) VALUES
(1, 'Swarna Shakti Organic Whole Wheat Atta 5kg', 1, 1, 1, 320.00, 299.00, 'Stone ground 100% whole wheat flour, high fiber, zero maida added.', 'India', 'https://retailbharat.in/p/swarna-atta-5kg', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(2, 'Swarna Shakti Premium Basmati Rice 1kg', 1, 1, 1, 195.00, 175.00, 'Aged royal long grain basmati rice, pristine aroma and delicate texture.', 'India', 'https://retailbharat.in/p/swarna-basmati-1kg', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(3, 'PureDrops Cold Pressed Mustard Oil 1L', 2, 1, 2, 210.00, 195.00, 'Traditional kachi ghani unrefined mustard oil rich in omega-3.', 'India', 'https://retailbharat.in/p/puredrops-mustard-1l', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(4, 'PureDrops Organic Desi Cow Ghee 500ml', 2, 3, 2, 450.00, 420.00, 'Traditional bilona method cultured A2 cow ghee with authentic aroma.', 'India', 'https://heritageagro.org/p/puredrops-ghee-500ml', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(5, 'Vedika Herbal Neem & Tulsi Purifying Face Wash 150ml', 3, 2, 4, 185.00, 160.00, 'Soap-free herbal face wash enriched with wild neem extract.', 'India', 'https://apexglobalkart.com/p/vedika-facewash-150', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(6, 'Vedika Ayurvedic Hair Vitalizer Oil 200ml', 3, 4, 4, 340.00, 299.00, 'Infused with bhringraj, amla and 18 restorative Himalayan herbs.', 'India', 'https://qcdirect.co/p/vedika-hair-oil-200ml', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(7, 'NutriBoost Plant Protein Powder Belgian Choc 1kg', 4, 2, 6, 2499.00, 2199.00, '25g protein per scoop with complete amino acid profile, vegan formula.', 'India', 'https://apexglobalkart.com/p/nutriboost-protein-1kg', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(8, 'Apex Glow Hydrating Sheet Mask 25g Pack of 5', 5, 5, 4, 650.00, 499.00, 'Deep moisturizing serum sheet mask with hyaluronic acid essence.', 'China', 'https://westernimports.biz/p/apex-sheetmask-pack5', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(9, 'Himalayan Spring Natural Mineral Water 1L Pack of 12', 6, 6, 10, 720.00, 600.00, 'Untouched mountain spring water bottled directly at source.', 'India', 'https://deccanconsumer.com/p/himalayan-water-12pk', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(10, 'EcoSpark Citrus Floor Cleaner Concentrated 2L', 7, 7, 8, 380.00, 320.00, 'Biodegradable non-toxic multi-surface disinfectant and cleaner.', 'India', 'https://easternretail.in/p/ecospark-cleaner-2l', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(11, 'LittleAngels Hypoallergenic Baby Wipes 80s', 8, 4, 5, 199.00, 149.00, '99% pure water wipes with organic aloe vera, dermatologically tested.', 'India', 'https://qcdirect.co/p/littleangels-wipes-80', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(12, 'UrbanRoast Arabica Dark Roast Coffee Beans 500g', 9, 3, 10, 599.00, 520.00, 'Single estate shade-grown Arabica whole beans from Baba Budan Giri.', 'India', 'https://heritageagro.org/p/urbanroast-coffee-500g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(13, 'Golden Harvest Guntur Red Chilli Powder 500g', 10, 8, 1, 240.00, 215.00, 'Pure fiery red chilli powder ground from premium dry sun-cured chillies.', 'India', 'https://northgateventures.in/p/golden-chilli-500g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(14, 'Golden Harvest Organic Turmeric Powder 200g', 10, 3, 1, 95.00, 85.00, 'High curcumin Lakadong organic turmeric powder with natural aroma.', 'India', 'https://heritageagro.org/p/golden-turmeric-200g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(15, 'VoltMax 65W GaN Fast Wall Charger', 11, 2, 7, 1899.00, 1499.00, 'Dual USB-C and USB-A high-efficiency compact travel charger.', 'China', 'https://apexglobalkart.com/p/voltmax-gan-charger', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(16, 'TrueNourish Adult Dog Dry Kibble Chicken 3kg', 12, 6, 9, 1250.00, 1099.00, 'Complete canine nutrition with real farm chicken and brown rice.', 'India', 'https://deccanconsumer.com/p/truenourish-dog-3kg', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(17, 'Swarna Shakti Refined Toor Dal 1kg', 1, 1, 1, 175.00, 160.00, 'Unpolished high protein yellow pigeon peas sourced from Gulbarga.', 'India', 'https://retailbharat.in/p/swarna-toor-dal-1kg', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(18, 'PureDrops Almond & Oat Milk Unsweetened 1L', 2, 3, 3, 290.00, 260.00, 'Nutrient dense lactose-free barista blend fortified with vitamins.', 'India', 'https://heritageagro.org/p/puredrops-almond-1l', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(19, 'Vedika Herbal Nourishing Lip Butter 10g', 3, 4, 4, 150.00, 130.00, 'Rich shea butter and cold pressed almond lip care balm.', 'India', 'https://qcdirect.co/p/vedika-lip-butter-10g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(20, 'Apex Glow Retinol Renewal Serum 30ml', 5, 5, 4, 899.00, 699.00, 'Encapsulated anti-aging facial serum with squalane and peptide complex.', 'China', 'https://westernimports.biz/p/apex-retinol-30ml', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(21, 'EcoSpark Natural Dishwash Gel Lemon 1L', 7, 7, 8, 220.00, 190.00, 'Tough on grease, gentle on hands with natural lemon peel extracts.', 'India', 'https://easternretail.in/p/ecospark-dishwash-1l', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(22, 'LittleAngels Gentle Head to Toe Baby Wash 400ml', 8, 4, 5, 399.00, 329.00, 'Tear-free hypoallergenic wash with organic calendula and chamomile.', 'India', 'https://qcdirect.co/p/littleangels-wash-400ml', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(23, 'NutriBoost Omega-3 Fish Oil 1000mg 60 Softgels', 4, 2, 6, 899.00, 699.00, 'High potency molecularly distilled wild ocean fish oil EPA/DHA.', 'India', 'https://apexglobalkart.com/p/nutriboost-omega3-60s', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(24, 'Golden Harvest Cumin Seeds Whole Jeera 250g', 10, 8, 1, 160.00, 140.00, 'Machine cleaned aromatic bold cumin seeds from Rajasthan.', 'India', 'https://northgateventures.in/p/golden-jeera-250g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(25, 'VoltMax Magnetic Wireless Power Bank 10000mAh', 11, 5, 7, 2499.00, 1999.00, 'Slim MagSafe compatible 20W PD portable battery pack.', 'China', 'https://westernimports.biz/p/voltmax-powerbank-10k', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(26, 'Swarna Shakti Roasted Chana Sattu 500g', 1, 1, 1, 110.00, 95.00, 'Traditional superfood drink mix rich in plant protein and dietary fiber.', 'India', 'https://retailbharat.in/p/swarna-sattu-500g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(27, 'PureDrops Paneer Fresh Vacuum Pack 200g', 2, 3, 3, 120.00, 110.00, 'Soft and creamy fresh cottage cheese made from farm fresh milk.', 'India', 'https://heritageagro.org/p/puredrops-paneer-200g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(28, 'Vedika Herbal Kumkumadi Glow Night Cream 50g', 3, 2, 4, 699.00, 549.00, 'Precious saffron and lotus infused traditional night rejuvenation cream.', 'India', 'https://apexglobalkart.com/p/vedika-kumkumadi-50g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(29, 'EcoSpark Laundry Detergent Liquid 2L', 7, 7, 8, 450.00, 399.00, 'Plant-derived optical-brightener free formula safe for infant wear.', 'India', 'https://easternretail.in/p/ecospark-laundry-2l', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(30, 'LittleAngels Baby Diaper Pants Size M 54 Count', 8, 1, 5, 899.00, 699.00, '12-hour leak protection with breathable cottony soft elastic waistband.', 'India', 'https://retailbharat.in/p/littleangels-diapers-m', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(31, 'UrbanRoast Filter Coffee Blend 80:20 500g', 9, 3, 10, 360.00, 320.00, 'Traditional South Indian filter coffee blend with plantation chicory.', 'India', 'https://heritageagro.org/p/urbanroast-filter-500g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(32, 'TrueNourish Gourmet Cat Wet Food Tuna In Jelly 85g', 12, 6, 9, 85.00, 75.00, 'Grain-free succulent wild tuna morsels in savory hydrating jelly.', 'India', 'https://deccanconsumer.com/p/truenourish-cat-85g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(33, 'VoltMax Ergonomic Wireless Vertical Mouse', 11, 2, 7, 1299.00, 999.00, '6-button silent optical vertical mouse with rechargeable battery.', 'China', 'https://apexglobalkart.com/p/voltmax-vert-mouse', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(34, 'Swarna Shakti Moong Dal Washed 1kg', 1, 1, 1, 160.00, 145.00, 'Easily digestible split yellow lentils, unpolished.', 'India', 'https://retailbharat.in/p/swarna-moong-1kg', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(35, 'PureDrops Probiotic Greek Yogurt Blueberry 100g', 2, 3, 3, 60.00, 55.00, 'Thick strained probiotic yogurt with real Alphonso and blueberry pulp.', 'India', 'https://heritageagro.org/p/puredrops-yogurt-100g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(36, 'Vedika Herbal Rose Water Toner 200ml', 3, 4, 4, 180.00, 150.00, 'Steam distilled Kannauj damask rose water for hydration and toning.', 'India', 'https://qcdirect.co/p/vedika-rose-water-200ml', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(37, 'NutriBoost Daily Multivitamin with Zinc 60 Tablets', 4, 2, 6, 599.00, 479.00, '36 essential vitamins and minerals for adult vitality and immune health.', 'India', 'https://apexglobalkart.com/p/nutriboost-multi-60t', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(38, 'Apex Glow Collagen Boosting Eye Cream 15ml', 5, 5, 4, 750.00, 599.00, 'Caffeine and marine collagen formula targeting dark circles and puffiness.', 'China', 'https://westernimports.biz/p/apex-eye-cream-15ml', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(39, 'EcoSpark Organic Kitchen Degreaser Spray 500ml', 7, 7, 8, 250.00, 210.00, 'Enzyme based fast action grease dissolver for chimneys and stovetops.', 'India', 'https://easternretail.in/p/ecospark-degreaser-500', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(40, 'Golden Harvest Coriander Powder Dhaniya 500g', 10, 8, 1, 170.00, 150.00, 'Coarsely ground fresh green coriander seeds with high essential oil.', 'India', 'https://northgateventures.in/p/golden-dhaniya-500g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(41, 'LittleAngels Organic Cotton Dry Feel Bibs Pack of 3', 8, 4, 5, 449.00, 379.00, 'Waterproof breathable feeding bibs with adjustable snap fasteners.', 'India', 'https://qcdirect.co/p/littleangels-bibs-3pk', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(42, 'UrbanRoast Cold Brew Concentrate Classic 250ml', 9, 3, 10, 300.00, 270.00, '18-hour cold steeped smooth artisan coffee extract, makes 5 servings.', 'India', 'https://heritageagro.org/p/urbanroast-coldbrew-250', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(43, 'TrueNourish Dental Chews for Dogs Medium 7 Sticks', 12, 6, 9, 350.00, 299.00, 'Veterinarian recommended tartar reducing daily dental treats.', 'India', 'https://deccanconsumer.com/p/truenourish-chews-7s', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(44, 'Swarna Shakti Organic Poha Thick Flattened Rice 500g', 1, 1, 1, 65.00, 58.00, 'Iron rich traditional breakfast flattened rice flakes from Madhya Pradesh.', 'India', 'https://retailbharat.in/p/swarna-poha-500g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(45, 'VoltMax 100W Braided USB-C to USB-C Cable 2m', 11, 2, 7, 699.00, 499.00, 'High durability nylon braided fast charging and data sync cable.', 'China', 'https://apexglobalkart.com/p/voltmax-cable-2m', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(46, 'PureDrops Organic Buffalo Ghee Granular 1L', 2, 3, 2, 850.00, 780.00, 'Traditional golden yellow aromatic granular buffalo ghee.', 'India', 'https://heritageagro.org/p/puredrops-buff-ghee-1l', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(47, 'Vedika Herbal Bhringraj Hair Conditioning Mask 200g', 3, 4, 4, 450.00, 380.00, 'Deep conditioning Ayurvedic hair spa cream for dry brittle strands.', 'India', 'https://qcdirect.co/p/vedika-hair-mask-200g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(48, 'Apex Glow Sunscreen Gel SPF 50 PA++++ 50g', 5, 5, 4, 599.00, 479.00, 'Ultra lightweight non-comedogenic invisible fluid sunscreen.', 'China', 'https://westernimports.biz/p/apex-sunscreen-50g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(49, 'NutriBoost Apple Cider Vinegar with Mother 500ml', 4, 2, 6, 499.00, 399.00, 'Raw unfiltered Himalayan apple cider vinegar naturally fermented.', 'India', 'https://apexglobalkart.com/p/nutriboost-acv-500ml', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(50, 'Golden Harvest Kashmiri Mirch Powder 100g', 10, 8, 1, 120.00, 105.00, 'Mild spicy rich ruby-red colouring whole ground chilli powder.', 'India', 'https://northgateventures.in/p/golden-kashmiri-100g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(51, 'EcoSpark Toilet Cleaner Herbal Power Shield 750ml', 7, 7, 8, 140.00, 120.00, 'Hydrochloric acid-free organic disinfectant gel with eucalyptus oils.', 'India', 'https://easternretail.in/p/ecospark-toilet-750', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP),
(52, 'LittleAngels Teething Gel Sugar Free 20g', 8, 4, 5, 180.00, 150.00, 'Soothing clove and licorice baby teething gum comfort gel.', 'India', 'https://qcdirect.co/p/littleangels-teething-20g', 'SIMULATED_CRAWL', 'SCANNED', CURRENT_TIMESTAMP);

-- 7. Seed Product Images for Products
INSERT INTO product_images (id, product_id, original_path, processed_path, image_type, uploaded_at) VALUES
(1, 1, 'storage/uploads/mock_label_atta_5kg.png', 'storage/uploads/mock_label_atta_5kg_proc.png', 'PACKAGE_LABEL', CURRENT_TIMESTAMP),
(2, 5, 'storage/uploads/mock_label_facewash_150ml.png', 'storage/uploads/mock_label_facewash_150ml_proc.png', 'PACKAGE_LABEL', CURRENT_TIMESTAMP),
(3, 8, 'storage/uploads/mock_label_sheetmask_25g.png', 'storage/uploads/mock_label_sheetmask_25g_proc.png', 'PACKAGE_LABEL', CURRENT_TIMESTAMP),
(4, 15, 'storage/uploads/mock_label_charger_65w.png', 'storage/uploads/mock_label_charger_65w_proc.png', 'PACKAGE_LABEL', CURRENT_TIMESTAMP),
(5, 7, 'storage/uploads/mock_label_protein_1kg.png', 'storage/uploads/mock_label_protein_1kg_proc.png', 'PACKAGE_LABEL', CURRENT_TIMESTAMP);

-- 8. Seed Compliance Checks with realistic scores and confidences
INSERT INTO compliance_checks (id, product_id, scan_job_id, overall_score, mandatory_field_completion_pct, critical_count, major_count, minor_count, ocr_confidence, ai_confidence, rule_engine_confidence, risk_level, summary_notes, created_at) VALUES
(1, 1, 1, 98.0, 100.0, 0, 0, 0, 94.5, 96.0, 100.0, 'LOW', 'Product meets all mandatory packaged commodity declaration criteria.', CURRENT_TIMESTAMP),
(2, 5, 2, 65.0, 71.4, 0, 2, 0, 88.0, 86.5, 95.0, 'MEDIUM', 'Potential non-compliance: Consumer care phone number unverified and non-standard unit detected on label.', CURRENT_TIMESTAMP),
(3, 8, 3, 35.0, 42.8, 2, 2, 0, 89.2, 85.0, 92.0, 'CRITICAL', 'Potential non-compliance: Missing country of origin, missing importer details, and MRP listing mismatch.', CURRENT_TIMESTAMP),
(4, 15, 4, 45.0, 57.1, 1, 2, 0, 91.0, 87.0, 90.0, 'HIGH', 'Potential non-compliance: Incomplete importer declaration and missing consumer care telephone.', CURRENT_TIMESTAMP),
(5, 7, 5, 50.0, 60.0, 1, 1, 0, 93.0, 90.0, 95.0, 'HIGH', 'Potential non-compliance: Package declared MRP ₹2199 vs listing claims MRP ₹2499.', CURRENT_TIMESTAMP);

-- 9. Seed Violations (Linked to Rules LM001 - LM010)
INSERT INTO violations (id, violation_id, product_id, rule_id, severity, detected_value, expected_condition, evidence_id, source, ocr_confidence, detection_confidence, timestamp, status, reviewer_id, reviewer_comment, resolution, resolution_timestamp) VALUES
(1, 'VIO-2026-0001', 8, 'LM004', 'MAJOR', 'Country of Origin not found in package declarations or listing', 'Declaration of Country of Origin is mandatory under Rule 6(1)(ac)', 1, 'IMAGE', 89.2, 92.0, CURRENT_TIMESTAMP, 'NEW', null, null, null, null),
(2, 'VIO-2026-0002', 8, 'LM001', 'CRITICAL', 'Manufacturer: Apex Global Guangzhou. Importer Name/Address missing.', 'Packaged commodities imported into India must bear name and address of Importer under Rule 6(1)(a)', 2, 'IMAGE', 87.5, 90.0, CURRENT_TIMESTAMP, 'UNDER_REVIEW', 2, 'Inspection in progress. Seller notice requested for import declaration proof.', null, null),
(3, 'VIO-2026-0003', 8, 'LM008', 'CRITICAL', 'Listing MRP: ₹650.00 | Physical Package MRP: ₹499.00 (Difference: +₹151.00)', 'Listing MRP must not exceed physical package declaration under Section 18/36', 3, 'LISTING', 94.0, 96.5, CURRENT_TIMESTAMP, 'CONFIRMED', 2, 'Regulator confirmed violation: Listing price inflated above statutory package MRP.', 'Notice of explanation served under Section 36(1) of Legal Metrology Act.', CURRENT_TIMESTAMP),
(4, 'VIO-2026-0004', 5, 'LM006', 'MAJOR', 'Unit declared as "150 ml."', 'Schedule II permits only standard symbol "ml" without trailing period or abbreviation', 4, 'IMAGE', 92.0, 88.0, CURRENT_TIMESTAMP, 'NEW', null, null, null, null),
(5, 'VIO-2026-0005', 7, 'LM008', 'CRITICAL', 'Listing MRP: ₹2499.00 | Physical Package MRP: ₹2199.00', 'Listing price exceeds marked MRP on physical package', 5, 'LISTING', 95.0, 98.0, CURRENT_TIMESTAMP, 'CONFIRMED', 2, 'Verified mismatch between physical pouch and digital product catalog.', 'Seller advised to reconcile catalog price.', CURRENT_TIMESTAMP);

-- 10. Seed Evidences
INSERT INTO evidences (id, violation_id, original_image_path, cropped_image_path, ocr_text, source_url_or_dataset, bounding_box, detection_timestamp) VALUES
(1, 1, 'storage/uploads/mock_label_sheetmask_25g.png', 'storage/uploads/crop_mask_origin.png', 'APEX GLOW FACIAL MASK / NET WT 25g / MFG FOR APEX GLOBAL GZ / BATCH 9821', 'https://westernimports.biz/p/apex-sheetmask-pack5', '120,450,300,80', CURRENT_TIMESTAMP),
(2, 2, 'storage/uploads/mock_label_sheetmask_25g.png', 'storage/uploads/crop_mask_mfg.png', 'MFG BY APEX GLOBAL FORMULATIONS INC / GUANGZHOU / NO IMPORTER STAMP FOUND', 'https://westernimports.biz/p/apex-sheetmask-pack5', '80,300,350,110', CURRENT_TIMESTAMP),
(3, 3, 'storage/uploads/mock_label_sheetmask_25g.png', 'storage/uploads/crop_mask_mrp.png', 'MRP RS. 499.00 INCL OF ALL TAXES / MFG 01/2026 / USE BEFORE 01/2028', 'https://westernimports.biz/p/apex-sheetmask-pack5', '400,600,280,75', CURRENT_TIMESTAMP),
(4, 4, 'storage/uploads/mock_label_facewash_150ml.png', 'storage/uploads/crop_facewash_unit.png', 'VEDIKA PURIFYING NEEM FACE WASH / NET VOL: 150 ml. / MRP RS 185', 'https://apexglobalkart.com/p/vedika-facewash-150', '210,510,220,60', CURRENT_TIMESTAMP),
(5, 5, 'storage/uploads/mock_label_protein_1kg.png', 'storage/uploads/crop_protein_mrp.png', 'MAX RETAIL PRICE RS. 2199.00 INCL ALL TAXES / NET WEIGHT 1 kg', 'https://apexglobalkart.com/p/nutriboost-protein-1kg', '310,720,290,85', CURRENT_TIMESTAMP);

-- 11. Seed Audit Logs
INSERT INTO audit_logs (user_id, username, action, entity, entity_id, timestamp, ip_address, old_value, new_value) VALUES
(2, 'regulator', 'LOGIN', 'USER', '2', CURRENT_TIMESTAMP, '127.0.0.1', null, '{"loginMethod":"PASSWORD","role":"REGULATOR"}'),
(2, 'regulator', 'VIOLATION_CONFIRMED', 'VIOLATION', 'VIO-2026-0003', CURRENT_TIMESTAMP, '127.0.0.1', '{"status":"UNDER_REVIEW"}', '{"status":"CONFIRMED","comment":"Regulator confirmed violation: Listing price inflated above statutory package MRP."}'),
(2, 'regulator', 'VIOLATION_CONFIRMED', 'VIOLATION', 'VIO-2026-0005', CURRENT_TIMESTAMP, '127.0.0.1', '{"status":"NEW"}', '{"status":"CONFIRMED","comment":"Verified mismatch between physical pouch and digital product catalog."}');

-- 12. Seed Notifications
INSERT INTO notifications (user_id, message, type, is_read, created_at) VALUES
(2, 'Critical MRP Mismatch detected on product "Apex Glow Retinol Renewal Serum 30ml". Potential Section 36 violation.', 'CRITICAL_ALERT', false, CURRENT_TIMESTAMP),
(2, 'Batch scan #JOB-884 completed: 12 products evaluated, 3 potential non-compliances flagged for review.', 'SCAN_COMPLETED', false, CURRENT_TIMESTAMP),
(2, 'Rule LM006 (Standard Unit of Measurement Compliance) flagged 2 potential violations.', 'RULE_TRIGGER', true, CURRENT_TIMESTAMP);

-- 13. Ensure sequence offsets for tables with explicit seed IDs
ALTER TABLE products ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE sellers ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE brands ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE categories ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE rules ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE product_images ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE compliance_checks ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE violations ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE evidences ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE audit_logs ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE notifications ALTER COLUMN id RESTART WITH 1000;

