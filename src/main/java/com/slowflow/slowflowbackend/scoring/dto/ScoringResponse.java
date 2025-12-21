package com.slowflow.slowflowbackend.scoring.dto;

import com.slowflow.slowflowbackend.rule.model.RuleCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ScoringResponse {
    private RuleCategory category;
    private String rawText;

    private int score;
    private String reason;

    private List<MatchedRule> matchedRules;

    @Getter
    @NoArgsConstructor
    public static class MatchedRule {
        private String ruleKey;
        private String axis;
        private String tag;
        private int score;
    }
}