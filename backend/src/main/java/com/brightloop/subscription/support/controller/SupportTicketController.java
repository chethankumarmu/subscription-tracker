package com.brightloop.subscription.support.controller;

import com.brightloop.subscription.common.security.SecurityUtils;
import com.brightloop.subscription.support.dto.*;
import com.brightloop.subscription.support.entity.TicketStatus;
import com.brightloop.subscription.support.service.SupportTicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support/tickets")
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
public class SupportTicketController {

    private final SupportTicketService ticketService;

    public SupportTicketController(SupportTicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<Page<TicketSummaryResponse>> listTickets(
            @RequestParam(required = false) TicketStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TicketSummaryResponse> tickets = ticketService.listTickets(status, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailResponse> getTicketDetail(@PathVariable Long id) {
        TicketDetailResponse ticket = ticketService.getTicketDetail(id);
        return ResponseEntity.ok(ticket);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TicketSummaryResponse> updateTicketStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketStatusRequest request) {
        TicketSummaryResponse updated = ticketService.updateTicketStatus(id, request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody AddCommentRequest request) {
        Long staffUserId = SecurityUtils.getCurrentUserId();
        CommentResponse comment = ticketService.addComment(id, staffUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }
}

