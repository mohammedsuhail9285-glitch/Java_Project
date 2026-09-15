package com.sih.navisense;

import com.sih.navisense.console.NavisenseConsoleApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

/**
 * NAVISENSE AI - Predictive Maritime Freight Forecasting & Vessel Charter Intelligence
 * Smart India Hackathon 2026 (SIH26006)
 * Ministry of Steel / Steel Authority of India Limited (SAIL)
 */
@SpringBootApplication
public class NavisenseApplication {

    public static void main(String[] args) {
        boolean isConsoleMode = Arrays.asList(args).contains("--console") ||
                                Arrays.asList(args).contains("-c") ||
                                Arrays.asList(args).contains("--demo") ||
                                Arrays.asList(args).contains("-d");

        if (isConsoleMode) {
            NavisenseConsoleApp.main(args);
        } else {
            SpringApplication.run(NavisenseApplication.class, args);
        }
    }
}
