package com.example.lmchecker.repository;

import com.example.lmchecker.entity.MaritimePort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaritimePortRepository extends JpaRepository<MaritimePort, Long> {
    Optional<MaritimePort> findByPortCode(String portCode);
    List<MaritimePort> findByCoast(String coast);
}

