package com.brightloop.subscription.catalog.controller;

import com.brightloop.subscription.catalog.dto.CreateProductRequest;
import com.brightloop.subscription.catalog.dto.ProductAdminDto;
import com.brightloop.subscription.catalog.dto.ProductPublicDto;
import com.brightloop.subscription.catalog.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductAdminDto> createProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductAdminDto> updateProduct(@PathVariable Long id, @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllProducts(Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ADMIN"));

        if (isAdmin) {
            List<ProductAdminDto> adminList = productService.getAllAdmin();
            return ResponseEntity.ok(adminList);
        } else {
            List<ProductPublicDto> publicList = productService.getAllPublic();
            return ResponseEntity.ok(publicList);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProductById(@PathVariable Long id, Authentication authentication) {
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ADMIN"));

        if (isAdmin) {
            return ResponseEntity.ok(productService.getByIdAdmin(id));
        } else {
            return ResponseEntity.ok(productService.getByIdPublic(id));
        }
    }
}

