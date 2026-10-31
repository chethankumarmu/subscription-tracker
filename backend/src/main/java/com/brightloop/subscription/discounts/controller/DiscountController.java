package com.brightloop.subscription.discounts.controller;

import com.brightloop.subscription.discounts.dto.DiscountDto;
import com.brightloop.subscription.discounts.dto.DiscountRequest;
import com.brightloop.subscription.discounts.entity.Discount;
import com.brightloop.subscription.discounts.repository.DiscountRedemptionRepository;
import com.brightloop.subscription.discounts.repository.DiscountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    private final DiscountRepository discountRepository;
    private final DiscountRedemptionRepository discountRedemptionRepository;

    public DiscountController(DiscountRepository discountRepository, DiscountRedemptionRepository discountRedemptionRepository) {
        this.discountRepository = discountRepository;
        this.discountRedemptionRepository = discountRedemptionRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DiscountDto>> getAll() {
        List<DiscountDto> list = discountRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/validate/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiscountDto> validate(@PathVariable String code) {
        Optional<Discount> discountOpt = discountRepository.findByCodeAndIsActiveTrue(code.toUpperCase());
        if (discountOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Discount d = discountOpt.get();
        OffsetDateTime now = OffsetDateTime.now();
        if (d.getStartsAt() != null && d.getStartsAt().isAfter(now)) {
            return ResponseEntity.notFound().build();
        }
        if (d.getEndsAt() != null && d.getEndsAt().isBefore(now)) {
            return ResponseEntity.notFound().build();
        }
        if (d.getUsageLimit() != null) {
            long used = discountRedemptionRepository.countByDiscountId(d.getId());
            if (used >= d.getUsageLimit()) {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok(toDto(d));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiscountDto> create(@RequestBody DiscountRequest req) {
        if ("PERCENT".equalsIgnoreCase(req.getDiscountType()) && (req.getValue() == null || req.getValue() < 1 || req.getValue() > 10000)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Percentage discount value must be between 1 and 10000 (bps)");
        }
        if ("FIXED".equalsIgnoreCase(req.getDiscountType()) && (req.getValue() == null || req.getValue() < 1)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fixed discount value must be greater than 0");
        }
        Discount d = Discount.builder()
                .code(req.getCode().toUpperCase())
                .name(req.getName())
                .discountType(req.getDiscountType().toUpperCase())
                .value(req.getValue())
                .scope(req.getScope() != null ? req.getScope().toUpperCase() : "SUBSCRIPTION")
                .startsAt(OffsetDateTime.now())
                .endsAt(req.getEndsAt() != null ? req.getEndsAt() : OffsetDateTime.now().plusMonths(3))
                .usageLimit(req.getUsageLimit() != null ? req.getUsageLimit() : 100)
                .isActive(true)
                .build();
        Discount saved = discountRepository.save(d);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiscountDto> deactivate(@PathVariable Long id) {
        Discount d = discountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discount not found"));
        d.setIsActive(false);
        Discount saved = discountRepository.save(d);
        return ResponseEntity.ok(toDto(saved));
    }

    private DiscountDto toDto(Discount d) {
        return new DiscountDto(
                d.getId(),
                d.getCode(),
                d.getName(),
                d.getDiscountType(),
                d.getValue(),
                d.getScope(),
                d.getStartsAt(),
                d.getEndsAt(),
                d.getUsageLimit(),
                d.getIsActive()
        );
    }
}
