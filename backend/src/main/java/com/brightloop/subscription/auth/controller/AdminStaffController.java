package com.brightloop.subscription.auth.controller;

import com.brightloop.subscription.auth.dto.CreateStaffRequest;
import com.brightloop.subscription.auth.dto.UserProfileResponse;
import com.brightloop.subscription.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/staff")
public class AdminStaffController {

    private final AuthService authService;

    public AdminStaffController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> createStaff(@RequestBody CreateStaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.createStaff(request));
    }
}

