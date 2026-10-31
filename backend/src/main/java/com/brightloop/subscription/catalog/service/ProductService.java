package com.brightloop.subscription.catalog.service;

import com.brightloop.subscription.catalog.dto.CreateProductRequest;
import com.brightloop.subscription.catalog.dto.ProductAdminDto;
import com.brightloop.subscription.catalog.dto.ProductPublicDto;
import com.brightloop.subscription.catalog.entity.Product;
import com.brightloop.subscription.catalog.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductAdminDto createProduct(CreateProductRequest req) {
        if (req.getSku() == null || req.getSku().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "SKU is required");
        }
        if (productRepository.existsBySku(req.getSku().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product SKU already exists");
        }

        OffsetDateTime now = OffsetDateTime.now();
        Long costPaise = req.getCostPricePaise() != null ? req.getCostPricePaise() : 0L;
        Long salesPaise = req.getSalesPricePaise() != null ? req.getSalesPricePaise() : 0L;

        Product product = new Product(
                null,
                req.getSku().trim(),
                req.getName(),
                req.getProductType() != null ? req.getProductType().toUpperCase() : "SERVICE",
                salesPaise,
                costPaise,
                req.getNotes(),
                true,
                now,
                now
        );
        Product saved = productRepository.save(product);
        return toAdminDto(saved);
    }

    @Transactional
    public ProductAdminDto updateProduct(Long id, CreateProductRequest req) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (req.getName() != null) product.setName(req.getName());
        if (req.getProductType() != null) product.setProductType(req.getProductType().toUpperCase());
        if (req.getSalesPricePaise() != null) product.setSalesPricePaise(req.getSalesPricePaise());
        if (req.getCostPricePaise() != null) product.setCostPricePaise(req.getCostPricePaise());
        if (req.getNotes() != null) product.setNotes(req.getNotes());
        product.setUpdatedAt(OffsetDateTime.now());

        Product saved = productRepository.save(product);
        return toAdminDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ProductAdminDto> getAllAdmin() {
        return productRepository.findAll().stream().map(this::toAdminDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductPublicDto> getAllPublic() {
        return productRepository.findAll().stream().map(this::toPublicDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductAdminDto getByIdAdmin(Long id) {
        return productRepository.findById(id)
                .map(this::toAdminDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    @Transactional(readOnly = true)
    public ProductPublicDto getByIdPublic(Long id) {
        return productRepository.findById(id)
                .map(this::toPublicDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public ProductAdminDto toAdminDto(Product p) {
        Long margin = (p.getSalesPricePaise() != null ? p.getSalesPricePaise() : 0L)
                - (p.getCostPricePaise() != null ? p.getCostPricePaise() : 0L);
        return new ProductAdminDto(
                p.getId(),
                p.getSku(),
                p.getName(),
                p.getProductType(),
                p.getSalesPricePaise(),
                p.getCostPricePaise(),
                margin,
                p.getNotes(),
                p.getIsActive()
        );
    }

    public ProductPublicDto toPublicDto(Product p) {
        return new ProductPublicDto(
                p.getId(),
                p.getSku(),
                p.getName(),
                p.getProductType(),
                p.getSalesPricePaise(),
                p.getNotes(),
                p.getIsActive()
        );
    }
}

