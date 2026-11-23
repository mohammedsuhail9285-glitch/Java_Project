package com.example.lmchecker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class ProductCreateRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private Long brandId;
    private String brandName;

    private Long sellerId;
    private String sellerName;

    private Long categoryId;
    private String categoryName;

    @NotNull(message = "Declared MRP is required")
    private Double declaredMrp;

    private Double listingPrice;
    private String description;
    private String countryOfOrigin = "India";
    private String productUrl;
    private String ingestionSource = "MANUAL";

    private Map<String, String> listingAttributes;

    public ProductCreateRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getBrandId() { return brandId; }
    public void setBrandId(Long brandId) { this.brandId = brandId; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Double getDeclaredMrp() { return declaredMrp; }
    public void setDeclaredMrp(Double declaredMrp) { this.declaredMrp = declaredMrp; }

    public Double getListingPrice() { return listingPrice; }
    public void setListingPrice(Double listingPrice) { this.listingPrice = listingPrice; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCountryOfOrigin() { return countryOfOrigin; }
    public void setCountryOfOrigin(String countryOfOrigin) { this.countryOfOrigin = countryOfOrigin; }

    public String getProductUrl() { return productUrl; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }

    public String getIngestionSource() { return ingestionSource; }
    public void setIngestionSource(String ingestionSource) { this.ingestionSource = ingestionSource; }

    public Map<String, String> getListingAttributes() { return listingAttributes; }
    public void setListingAttributes(Map<String, String> listingAttributes) { this.listingAttributes = listingAttributes; }
}
