package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "brands")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(name = "manufacturer_name", length = 255)
    private String manufacturerName;

    @Column(name = "country_of_origin", length = 64)
    private String countryOfOrigin = "India";

    @Column(name = "total_products", nullable = false)
    private Integer totalProducts = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Brand() {}

    public Brand(Long id, String name, String manufacturerName, String countryOfOrigin, Integer totalProducts, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.manufacturerName = manufacturerName;
        this.countryOfOrigin = countryOfOrigin != null ? countryOfOrigin : "India";
        this.totalProducts = totalProducts != null ? totalProducts : 0;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }

    public String getCountryOfOrigin() { return countryOfOrigin; }
    public void setCountryOfOrigin(String countryOfOrigin) { this.countryOfOrigin = countryOfOrigin; }

    public Integer getTotalProducts() { return totalProducts; }
    public void setTotalProducts(Integer totalProducts) { this.totalProducts = totalProducts; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String manufacturerName;
        private String countryOfOrigin = "India";
        private Integer totalProducts = 0;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder manufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; return this; }
        public Builder countryOfOrigin(String countryOfOrigin) { this.countryOfOrigin = countryOfOrigin; return this; }
        public Builder totalProducts(Integer totalProducts) { this.totalProducts = totalProducts; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Brand build() {
            return new Brand(id, name, manufacturerName, countryOfOrigin, totalProducts, createdAt);
        }
    }
}
