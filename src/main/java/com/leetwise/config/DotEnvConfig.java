package com.leetwise.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class DotEnvConfig {

    @PostConstruct
    public void loadEnvVariables() {
        try {
            Path envFile = Paths.get(".env");
            if (Files.exists(envFile)) {
                Files.lines(envFile)
                    .filter(line -> line.contains("=") && !line.startsWith("#"))
                    .forEach(line -> {
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            System.setProperty(parts[0].trim(), parts[1].trim());
                        }
                    });
            }
        } catch (IOException e) {
            // Handle exception
        }
    }
}
