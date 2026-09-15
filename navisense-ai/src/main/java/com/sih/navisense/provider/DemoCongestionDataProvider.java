package com.sih.navisense.provider;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;

@Component
public class DemoCongestionDataProvider implements CongestionDataProvider {

    private final Map<String, Double> congestionMap = new HashMap<>();
    private final Map<String, Double> waitDaysMap = new HashMap<>();

    public DemoCongestionDataProvider() {
        // Representative Indian East Coast Ports Congestion Scores (0 - 100) and wait days
        congestionMap.put("PARADIP", 55.0);
        waitDaysMap.put("PARADIP", 2.2);

        congestionMap.put("VISAKHAPATNAM", 42.0);
        waitDaysMap.put("VISAKHAPATNAM", 1.5);

        congestionMap.put("GANGAVARAM", 35.0);
        waitDaysMap.put("GANGAVARAM", 1.1);

        congestionMap.put("DHAMRA", 38.0);
        waitDaysMap.put("DHAMRA", 1.2);

        congestionMap.put("GOPALPUR", 28.0);
        waitDaysMap.put("GOPALPUR", 0.8);

        congestionMap.put("HALDIA", 68.0);
        waitDaysMap.put("HALDIA", 3.4);

        congestionMap.put("SAGAR", 45.0);
        waitDaysMap.put("SAGAR", 1.8);
    }

    @Override
    public double getCongestionIndex(String portName) {
        if (portName == null) return 40.0;
        return congestionMap.getOrDefault(portName.toUpperCase().trim(), 40.0);
    }

    @Override
    public double getEstimatedWaitDays(String portName) {
        if (portName == null) return 1.5;
        return waitDaysMap.getOrDefault(portName.toUpperCase().trim(), 1.5);
    }
}
