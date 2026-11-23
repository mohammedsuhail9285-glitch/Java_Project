package com.example.lmchecker.repository;

import com.example.lmchecker.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE " +
           "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.countryOfOrigin) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:categoryId IS NULL OR p.categoryId = :categoryId) AND " +
           "(:sellerId IS NULL OR p.sellerId = :sellerId) AND " +
           "(:brandId IS NULL OR p.brandId = :brandId) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Product> searchProducts(
        @Param("keyword") String keyword,
        @Param("categoryId") Long categoryId,
        @Param("sellerId") Long sellerId,
        @Param("brandId") Long brandId,
        @Param("status") String status,
        Pageable pageable
    );

    List<Product> findBySellerId(Long sellerId);
    List<Product> findByBrandId(Long brandId);
    List<Product> findByCategoryId(Long categoryId);
    long countByStatus(String status);
}
