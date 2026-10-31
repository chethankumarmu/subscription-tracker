package com.brightloop.subscription.controller;

import com.brightloop.subscription.dto.SubscriptionRequest;
import com.brightloop.subscription.model.Subscription;
import com.brightloop.subscription.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController("apiSubscriptionController")
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "*")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(@org.springframework.beans.factory.annotation.Qualifier("apiSubscriptionService") SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    @PostMapping
    public ResponseEntity<Subscription> create(@RequestBody SubscriptionRequest request) {
        return ResponseEntity.ok(subscriptionService.createSubscription(request));
    }

    @PutMapping("/{id}/pause")
    public ResponseEntity<Subscription> togglePause(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.togglePause(id));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<Subscription> close(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : "Closed by user";
        return ResponseEntity.ok(subscriptionService.closeSubscription(id, reason));
    }
}
