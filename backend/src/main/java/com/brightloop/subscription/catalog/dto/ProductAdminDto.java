package com.brightloop.subscription.catalog.dto;

public class ProductAdminDto {
    private Long id;
    private String sku;
    private String name;
    private String productType;
    private Long salesPricePaise;
    private Long costPricePaise;
    private Long marginPaise;
    private String notes;
    private Boolean isActive;

    public ProductAdminDto() {}

    public ProductAdminDto(Long id, String sku, String name, String productType, Long salesPricePaise, Long costPricePaise, Long marginPaise, String notes, Boolean isActive) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.productType = productType;
        this.salesPricePaise = salesPricePaise;
        this.costPricePaise = costPricePaise;
        this.marginPaise = marginPaise;
        this.notes = notes;
        this.isActive = isActive;
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getProductType() { return productType; }
    public Long getSalesPricePaise() { return salesPricePaise; }
    public Long getCostPricePaise() { return costPricePaise; }
    public Long getMarginPaise() { return marginPaise; }
    public String getNotes() { return notes; }
    public Boolean getIsActive() { return isActive; }
}

