package com.brightloop.subscription.chatbot.controller;

import com.brightloop.subscription.chatbot.dto.ChatResponse;
import com.brightloop.subscription.chatbot.dto.ChatSessionDetailResponse;
import com.brightloop.subscription.chatbot.dto.SendMessageRequest;
import com.brightloop.subscription.chatbot.entity.ChatSession;
import com.brightloop.subscription.chatbot.service.ChatbotService;
import com.brightloop.subscription.common.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat/sessions")
@PreAuthorize("hasRole('CUSTOMER')")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ResponseEntity<ChatSessionDetailResponse> createOrGetSession() {
        Long userId = SecurityUtils.getCurrentUserId();
        Long customerId = SecurityUtils.requireCurrentCustomerId();

        ChatSession session = chatbotService.createOrGetActiveSession(userId, customerId);
        ChatSessionDetailResponse response = chatbotService.getSessionHistory(session.getId(), customerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ChatSessionDetailResponse> getSession(@PathVariable Long sessionId) {
        Long customerId = SecurityUtils.requireCurrentCustomerId();
        ChatSessionDetailResponse response = chatbotService.getSessionHistory(sessionId, customerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sessionId}/messages")
    public ResponseEntity<ChatResponse> sendMessage(
            @PathVariable Long sessionId,
            @Valid @RequestBody SendMessageRequest request) {
        Long customerId = SecurityUtils.requireCurrentCustomerId();
        ChatResponse response = chatbotService.processCustomerMessage(sessionId, customerId, request.getMessage());
        return ResponseEntity.ok(response);
    }
}

