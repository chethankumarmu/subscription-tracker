package com.brightloop.subscription.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        File[] candidates = new File[] {
                new File(".env"),
                new File("backend/.env"),
                new File("../backend/.env")
        };

        for (File envFile : candidates) {
            if (envFile.exists() && envFile.isFile()) {
                try {
                    List<String> lines = Files.readAllLines(envFile.toPath());
                    Map<String, Object> props = new HashMap<>();
                    for (String line : lines) {
                        String trimmed = line.trim();
                        if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                            int eq = trimmed.indexOf('=');
                            if (eq > 0) {
                                String key = trimmed.substring(0, eq).trim();
                                String value = trimmed.substring(eq + 1).trim();
                                props.put(key, value);
                            }
                        }
                    }
                    if (!props.isEmpty()) {
                        environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties-" + envFile.getPath(), props));
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }
}
