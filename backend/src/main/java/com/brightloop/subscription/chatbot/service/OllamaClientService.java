package com.brightloop.subscription.chatbot.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
            @Value("${app.ollama.timeout-ms:5000}") int timeoutMs) {
        this.model = model;
        this.enabled = baseUrl != null && !baseUrl.isBlank();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(Math.min(timeoutMs, 3000)));
        requestFactory.setReadTimeout(Duration.ofMillis(timeoutMs));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public Optional<String> generateAnswer(String systemPrompt, String userMessage) {
        return generateResponse(systemPrompt, userMessage);
    }

    public Optional<String> generateResponse(String systemPrompt, String userMessage) {
        if (!enabled) {
            return Optional.empty();
        }

        try {
            OllamaChatRequest request = OllamaChatRequest.builder()
                    .model(this.model)
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

    public static class OllamaChatRequest {
        private String model;
        private boolean stream;
        private List<OllamaChatMessage> messages;

        public OllamaChatRequest() {}
        public OllamaChatRequest(String model, boolean stream, List<OllamaChatMessage> messages) {
            this.model = model;
            this.stream = stream;
            this.messages = messages;
        }

        public static OllamaChatRequestBuilder builder() { return new OllamaChatRequestBuilder(); }
        public static class OllamaChatRequestBuilder {
            private String model;
            private boolean stream;
            private List<OllamaChatMessage> messages;
            public OllamaChatRequestBuilder model(String model) { this.model = model; return this; }
            public OllamaChatRequestBuilder stream(boolean stream) { this.stream = stream; return this; }
            public OllamaChatRequestBuilder messages(List<OllamaChatMessage> messages) { this.messages = messages; return this; }
            public OllamaChatRequest build() { return new OllamaChatRequest(model, stream, messages); }
        }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public boolean isStream() { return stream; }
        public void setStream(boolean stream) { this.stream = stream; }
        public List<OllamaChatMessage> getMessages() { return messages; }
        public void setMessages(List<OllamaChatMessage> messages) { this.messages = messages; }
    }

    public static class OllamaChatMessage {
        private String role;
        private String content;

        public OllamaChatMessage() {}
        public OllamaChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class OllamaChatResponse {
        private String model;
        @JsonProperty("created_at")
        private String createdAt;
        private OllamaChatMessage message;
        private boolean done;

        public OllamaChatResponse() {}
        public OllamaChatResponse(String model, String createdAt, OllamaChatMessage message, boolean done) {
            this.model = model;
            this.createdAt = createdAt;
            this.message = message;
            this.done = done;
        }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public OllamaChatMessage getMessage() { return message; }
        public void setMessage(OllamaChatMessage message) { this.message = message; }
        public boolean isDone() { return done; }
        public void setDone(boolean done) { this.done = done; }
    }
}
