package com.example.lmchecker.repository;

import com.example.lmchecker.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {

    Optional<OtpCode> findFirstByIdentifierAndPurposeAndConsumedFalseOrderByCreatedAtDesc(String identifier, String purpose);

    List<OtpCode> findByIdentifierAndPurposeAndConsumedFalseOrderByCreatedAtDesc(String identifier, String purpose);

    long countByIdentifierAndCreatedAtAfter(String identifier, LocalDateTime after);
}
