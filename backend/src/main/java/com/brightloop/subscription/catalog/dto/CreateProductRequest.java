package com.brightloop.subscription.catalog.dto;

public class CreateProductRequest {
    private String sku;
    private String name;
    private String productType;
    private Long salesPricePaise;
    private Long costPricePaise;
    private String notes;

    public CreateProductRequest() {}

    public CreateProductRequest(String sku, String name, String productType, Long salesPricePaise, Long costPricePaise, String notes) {
        this.sku = sku;
        this.name = name;
        this.productType = productType;
        this.salesPricePaise = salesPricePaise;
        this.costPricePaise = costPricePaise;
        this.notes = notes;
    }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }
    public Long getSalesPricePaise() { return salesPricePaise; }
    public void setSalesPricePaise(Long salesPricePaise) { this.salesPricePaise = salesPricePaise; }
    public Long getCostPricePaise() { return costPricePaise; }
    public void setCostPricePaise(Long costPricePaise) { this.costPricePaise = costPricePaise; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

