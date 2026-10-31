package com.brightloop.subscription.controller;

import com.brightloop.subscription.dto.ChatMessageRequest;
import com.brightloop.subscription.dto.ChatMessageResponse;
import com.brightloop.subscription.model.FaqArticle;
import com.brightloop.subscription.model.SupportTicket;
import com.brightloop.subscription.repository.FaqArticleRepository;
import com.brightloop.subscription.service.SupportChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
@CrossOrigin(origins = "*")
public class SupportController {

    private final SupportChatbotService supportChatbotService;
    private final FaqArticleRepository faqArticleRepository;

    public SupportController(SupportChatbotService supportChatbotService, FaqArticleRepository faqArticleRepository) {
        this.supportChatbotService = supportChatbotService;
        this.faqArticleRepository = faqArticleRepository;
    }

    @GetMapping("/faqs")
    public ResponseEntity<List<FaqArticle>> getFaqs() {
        return ResponseEntity.ok(faqArticleRepository.findByIsPublishedTrue());
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatMessageResponse> chat(@RequestBody ChatMessageRequest request) {
        return ResponseEntity.ok(supportChatbotService.processMessage(request));
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<SupportTicket>> getTickets() {
        return ResponseEntity.ok(supportChatbotService.getAllTickets());
    }

    @PutMapping("/tickets/{id}/status")
    public ResponseEntity<SupportTicket> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "OPEN");
        return ResponseEntity.ok(supportChatbotService.updateTicketStatus(id, status));
    }
}

