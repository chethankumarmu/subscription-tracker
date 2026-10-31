package com.subscriptiontracker.subscription;

import com.subscriptiontracker.subscription.SubscriptionService.CreateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final SubscriptionService service;

    public SubscriptionController(SubscriptionService service) { this.service = service; }

    @GetMapping
    public List<Subscription> list(@RequestParam(required = false) Long customerId) {
        return service.list(customerId);
    }

    @GetMapping("/{id}")
    public Subscription get(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Subscription create(@Valid @RequestBody CreateRequest r) { return service.createDraft(r); }

    @PostMapping("/{id}/quotation")
    @PreAuthorize("hasRole('ADMIN')")
    public Subscription quotation(@PathVariable Long id, @RequestParam Long templateId) {
        return service.generateQuotation(id, templateId);
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public Subscription confirm(@PathVariable Long id) { return service.confirm(id); }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public Subscription activate(@PathVariable Long id) { return service.activate(id); }

    @PostMapping("/{id}/pause")
    @PreAuthorize("hasRole('ADMIN')")
    public Subscription pause(@PathVariable Long id) { return service.pause(id); }

    @PostMapping("/{id}/resume")
    @PreAuthorize("hasRole('ADMIN')")
    public Subscription resume(@PathVariable Long id) { return service.resume(id); }

    @PostMapping("/{id}/renew")
    @PreAuthorize("hasRole('ADMIN')")
    public Subscription renew(@PathVariable Long id) { return service.renew(id); }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public Subscription close(@PathVariable Long id) { return service.close(id); }
}
