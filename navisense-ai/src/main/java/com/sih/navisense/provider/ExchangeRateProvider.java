package com.sih.navisense.provider;

import java.math.BigDecimal;

public interface ExchangeRateProvider {
    BigDecimal getUsdToInrRate();
    BigDecimal convertUsdToInr(BigDecimal amountInUsd);
}
