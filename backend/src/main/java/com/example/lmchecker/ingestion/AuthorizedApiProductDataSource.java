package com.example.lmchecker.ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Enterprise integration stub for official marketplace API partnerships
 * (e.g. ONDC, authorized e-commerce partner catalog feeds).
 */
@Component
public class AuthorizedApiProductDataSource implements ProductDataSource {

    private static final Logger log = LoggerFactory.getLogger(AuthorizedApiProductDataSource.class);

    @Override
    public String getSourceType() {
        return "AUTHORIZED_MARKETPLACE_API";
    }

    @Override
    public IngestedProductData fetchProductFromUrl(String url) {
        log.info("[API STUB] Resolving product via authorized marketplace partner API: {}", url);
        return null;
    }

    @Override
    public List<IngestedProductData> fetchSimulatedCrawlBatch(int count) {
        return Collections.emptyList();
    }
}
