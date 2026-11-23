package com.example.lmchecker.repository;

import com.example.lmchecker.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {
    Optional<Rule> findByRuleId(String ruleId);
    List<Rule> findByEnabledTrue();
    List<Rule> findByCategory(String category);
}
