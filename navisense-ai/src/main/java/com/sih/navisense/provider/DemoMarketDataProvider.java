package com.sih.navisense.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DemoMarketDataProvider implements MarketDataProvider {

    private final double bunkerFuelPriceUsd;

    public DemoMarketDataProvider(@Value("${navisense.bunker.fuel-price-usd:620.00}") double bunkerFuelPriceUsd) {
        this.bunkerFuelPriceUsd = bunkerFuelPriceUsd;
    }

    @Override
    public double getBalticDryIndexEquivalent() {
        // Representative BDI proxy value for demo
        return 1865.0;
    }

    @Override
    public double getBunkerFuelPricePerTonneUsd() {
        return bunkerFuelPriceUsd;
    }

    @Override
    public double getMarketVolatilityIndex() {
        return 18.4; // 18.4% annualized representative freight volatility
    }
}
