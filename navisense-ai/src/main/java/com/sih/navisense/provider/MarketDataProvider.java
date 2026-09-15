package com.sih.navisense.provider;

public interface MarketDataProvider {
    double getBalticDryIndexEquivalent();
    double getBunkerFuelPricePerTonneUsd();
    double getMarketVolatilityIndex();
}
