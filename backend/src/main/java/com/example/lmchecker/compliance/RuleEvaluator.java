package com.example.lmchecker.compliance;

import com.example.lmchecker.entity.Rule;
import com.example.lmchecker.entity.ValidationType;

public interface RuleEvaluator {
    ValidationType getSupportedType();
    RuleResult evaluate(Rule rule, ProductComplianceContext context);
}
