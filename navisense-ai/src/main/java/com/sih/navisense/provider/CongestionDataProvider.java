package com.sih.navisense.provider;

public interface CongestionDataProvider {
    double getCongestionIndex(String portName);
    double getEstimatedWaitDays(String portName);
}
