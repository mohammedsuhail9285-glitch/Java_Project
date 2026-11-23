package com.example.lmchecker.repository;

import com.example.lmchecker.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    List<Seller> findByOrderByRiskScoreDesc();
}
