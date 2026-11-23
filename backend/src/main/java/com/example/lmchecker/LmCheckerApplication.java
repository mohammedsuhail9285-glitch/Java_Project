package com.example.lmchecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LmCheckerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LmCheckerApplication.class, args);
    }
}
