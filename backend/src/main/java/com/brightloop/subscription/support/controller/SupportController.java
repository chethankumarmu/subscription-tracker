package com.brightloop.subscription.support.controller;

import com.brightloop.subscription.auth.security.UserPrincipal;
import com.brightloop.subscription.support.dto.*;
import com.brightloop.subscription.support.service.SupportChatbotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    private final SupportChatbotService supportChatbotService;

    public SupportController(SupportChatbotService supportChatbotService) {
        this.supportChatbotService = supportChatbotService;
    }

    @GetMapping("/faqs")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FaqDto>> getFaqs() {
        return ResponseEntity.ok(supportChatbotService.getPublishedFaqs());
    }

    @PostMapping("/chat")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatMessageResponse> chat(
            @RequestBody ChatMessageRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        if (principal != null && "CUSTOMER".equalsIgnoreCase(principal.getRole())) {
            request.setCustomerId(principal.getCustomerId());
        }
        return ResponseEntity.ok(supportChatbotService.processMessage(request));
    }

    @GetMapping("/tickets")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<List<SupportTicketDto>> getTickets(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(supportChatbotService.getAllTickets(principal));
    }

    @GetMapping("/tickets/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<SupportTicketDto> getTicketById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(supportChatbotService.getTicketById(id, principal));
    }

    @PostMapping("/tickets/{id}/comments")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<TicketCommentDto> addComment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        String messageBody = (String) body.getOrDefault("body", "");
        Boolean isInternal = (Boolean) body.getOrDefault("isInternal", false);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supportChatbotService.addComment(id, messageBody, isInternal, principal));
    }

    @PutMapping("/tickets/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<SupportTicketDto> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "OPEN");
        return ResponseEntity.ok(supportChatbotService.updateTicketStatus(id, status));
    }
}
