package com.sih.navisense.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ConfiguredExchangeRateProvider implements ExchangeRateProvider {

    private final BigDecimal usdToInrRate;

    public ConfiguredExchangeRateProvider(@Value("${navisense.currency.usd-inr:85.50}") double rate) {
        this.usdToInrRate = BigDecimal.valueOf(rate).setScale(4, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getUsdToInrRate() {
        return usdToInrRate;
    }

    @Override
    public BigDecimal convertUsdToInr(BigDecimal amountInUsd) {
        if (amountInUsd == null) {
            return BigDecimal.ZERO;
        }
        return amountInUsd.multiply(usdToInrRate).setScale(2, RoundingMode.HALF_UP);
    }
}
