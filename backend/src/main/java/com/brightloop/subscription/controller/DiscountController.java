package com.brightloop.subscription.controller;

import com.brightloop.subscription.dto.DiscountRequest;
import com.brightloop.subscription.model.Discount;
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

    public DiscountController(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @GetMapping
    public ResponseEntity<List<Discount>> getAll() {
        return ResponseEntity.ok(discountRepository.findAll());
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<Discount> validate(@PathVariable String code) {
        Optional<Discount> discount = discountRepository.findByCodeAndIsActiveTrue(code.toUpperCase());
        return discount.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Discount> create(@RequestBody DiscountRequest req) {
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
