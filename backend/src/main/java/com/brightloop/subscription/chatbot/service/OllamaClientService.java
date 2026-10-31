package com.brightloop.subscription.chatbot.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class OllamaClientService {

    private static final Logger log = LoggerFactory.getLogger(OllamaClientService.class);

    private final RestClient restClient;
    private final String model;
    private final boolean enabled;

    public OllamaClientService(
            @Value("${app.ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${app.ollama.model:qwen2.5:3b}") String model,
            @Value("${app.ollama.timeout-ms:5000}") long timeoutMs) {
        this.model = model;
        this.enabled = true;

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(timeoutMs))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofMillis(timeoutMs));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public OllamaClientService(RestClient restClient, String model) {
        this.restClient = restClient;
        this.model = model;
        this.enabled = true;
    }

    public Optional<String> generateAnswer(String systemPrompt, String userMessage) {
        if (!enabled) {
            return Optional.empty();
        }

        try {
            OllamaChatRequest request = OllamaChatRequest.builder()
                    .model(model)
                    .stream(false)
                    .messages(List.of(
                            new OllamaChatMessage("system", systemPrompt),
                            new OllamaChatMessage("user", userMessage)
                    ))
                    .build();

            OllamaChatResponse response = restClient.post()
                    .uri("/api/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(OllamaChatResponse.class);

            if (response != null && response.getMessage() != null && response.getMessage().getContent() != null) {
                String content = response.getMessage().getContent().trim();
                if (!content.isEmpty()) {
                    return Optional.of(content);
                }
            }
        } catch (Exception e) {
            log.warn("Ollama invocation failed or timed out (falling back to deterministic answer): {}", e.getMessage());
        }

        return Optional.empty();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OllamaChatRequest {
        private String model;
        private boolean stream;
        private List<OllamaChatMessage> messages;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OllamaChatMessage {
        private String role;
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OllamaChatResponse {
        private String model;
        @JsonProperty("created_at")
        private String createdAt;
        private OllamaChatMessage message;
        private boolean done;
    }
}

