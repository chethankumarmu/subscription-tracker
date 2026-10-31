package com.brightloop.subscription.catalog.dto;

public class ProductPublicDto {
    private Long id;
    private String sku;
    private String name;
    private String productType;
    private Long salesPricePaise;
    private String notes;
    private Boolean isActive;

    public ProductPublicDto() {}

    public ProductPublicDto(Long id, String sku, String name, String productType, Long salesPricePaise, String notes, Boolean isActive) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.productType = productType;
        this.salesPricePaise = salesPricePaise;
        this.notes = notes;
        this.isActive = isActive;
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getProductType() { return productType; }
    public Long getSalesPricePaise() { return salesPricePaise; }
    public String getNotes() { return notes; }
    public Boolean getIsActive() { return isActive; }
}

