package com.example.lmchecker.ingestion;

import java.util.List;
import java.util.Map;

public interface ProductDataSource {
    String getSourceType();
    IngestedProductData fetchProductFromUrl(String url);
    List<IngestedProductData> fetchSimulatedCrawlBatch(int count);

    record IngestedProductData(
        String name,
        String brandName,
        String sellerName,
        String categoryName,
        Double declaredMrp,
        Double listingPrice,
        String description,
        String countryOfOrigin,
        String productUrl,
        String imagePath,
        Map<String, String> listingAttributes
    ) {}
}
