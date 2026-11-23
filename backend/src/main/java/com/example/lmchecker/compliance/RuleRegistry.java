package com.example.lmchecker.compliance;

import com.example.lmchecker.entity.ValidationType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class RuleRegistry {

    private final Map<ValidationType, RuleEvaluator> evaluatorMap = new EnumMap<>(ValidationType.class);

    public RuleRegistry(List<RuleEvaluator> evaluators) {
        for (RuleEvaluator e : evaluators) {
            evaluatorMap.put(e.getSupportedType(), e);
        }
    }

    public RuleEvaluator getEvaluator(ValidationType type) {
        return evaluatorMap.get(type);
    }
}
