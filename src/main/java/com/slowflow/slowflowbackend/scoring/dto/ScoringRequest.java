package com.slowflow.slowflowbackend.scoring.dto;

import com.slowflow.slowflowbackend.rule.model.RuleCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScoringRequest {
    private RuleCategory category;
    private String text;

    // FastAPI는 순수 채점(score, reason)만 내려주고,
    // CAP/CLAMP/상태 반영은 Spring이 하도록 current는 안 보냄.
}