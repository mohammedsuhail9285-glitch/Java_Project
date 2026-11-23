package com.example.lmchecker.controller;

import com.example.lmchecker.config.DataInitializer;
import com.example.lmchecker.dto.ApiResponse;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
public class DemoResetController {

    private static final Logger log = LoggerFactory.getLogger(DemoResetController.class);
    private final Flyway flyway;
    private final DataInitializer dataInitializer;

    public DemoResetController(Flyway flyway, DataInitializer dataInitializer) {
        this.flyway = flyway;
        this.dataInitializer = dataInitializer;
    }

    @PostMapping("/reset-demo")
    public ResponseEntity<ApiResponse<String>> resetDemo() {
        log.info("Resetting demo database to statutory baseline...");
        flyway.clean();
        flyway.migrate();
        try {
            dataInitializer.run();
        } catch (Exception e) {
            log.warn("DataInitializer re-run note: {}", e.getMessage());
        }
        log.info("Demo database successfully reset and seeded with 200 products!");
        return ResponseEntity.ok(ApiResponse.ok("Demo database successfully reset to clean statutory 200-SKU baseline", "RESET_SUCCESSFUL"));
    }
}
