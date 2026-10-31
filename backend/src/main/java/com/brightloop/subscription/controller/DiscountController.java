package com.brightloop.subscription.controller;

import com.brightloop.subscription.dto.DiscountRequest;
import com.brightloop.subscription.model.Discount;
import com.brightloop.subscription.repository.DiscountRedemptionRepository;
import com.brightloop.subscription.repository.DiscountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/discounts")
@CrossOrigin(origins = "*")
public class DiscountController {

    private final DiscountRepository discountRepository;
    private final DiscountRedemptionRepository discountRedemptionRepository;

    public DiscountController(DiscountRepository discountRepository, DiscountRedemptionRepository discountRedemptionRepository) {
        this.discountRepository = discountRepository;
        this.discountRedemptionRepository = discountRedemptionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Discount>> getAll() {
        return ResponseEntity.ok(discountRepository.findAll());
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<Discount> validate(@PathVariable String code) {
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
        return ResponseEntity.ok(d);
    }

    @PostMapping
    public ResponseEntity<Discount> create(@RequestBody DiscountRequest req) {
        if ("PERCENT".equalsIgnoreCase(req.getDiscountType()) && (req.getValue() == null || req.getValue() < 1 || req.getValue() > 10000)) {
            throw new IllegalArgumentException("Percentage discount value must be between 1 and 10000 (bps)");
        }
        if ("FIXED".equalsIgnoreCase(req.getDiscountType()) && (req.getValue() == null || req.getValue() < 1)) {
            throw new IllegalArgumentException("Fixed discount value must be greater than 0");
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
        return ResponseEntity.ok(discountRepository.save(d));
    }
}

