package com.example.lmchecker.repository;

import com.example.lmchecker.entity.ProductListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductListingRepository extends JpaRepository<ProductListing, Long> {
    List<ProductListing> findByProductId(Long productId);
    Optional<ProductListing> findFirstByProductIdOrderByScrapedAtDesc(Long productId);
}
