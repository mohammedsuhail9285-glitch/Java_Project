package com.example.lmchecker.repository;

import com.example.lmchecker.entity.RuleVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleVersionRepository extends JpaRepository<RuleVersion, Long> {
    List<RuleVersion> findByRuleIdOrderByVersionNumberDesc(String ruleId);
}
