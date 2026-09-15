package com.sih.navisense.repository;

import com.sih.navisense.entity.CharterDecisionRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharterDecisionRecordRepository extends JpaRepository<CharterDecisionRecord, Long> {
    Optional<CharterDecisionRecord> findByFingerprint(String fingerprint);
    List<CharterDecisionRecord> findAllByOrderByDecisionTimestampDesc(Pageable pageable);
}
