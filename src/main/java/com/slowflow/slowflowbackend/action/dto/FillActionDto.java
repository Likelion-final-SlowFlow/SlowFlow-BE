package com.slowflow.slowflowbackend.action.dto;

import com.slowflow.slowflowbackend.rule.model.RuleCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FillActionDto {
    private Long id;
    private RuleCategory category;
    private String behavior;
    private int score;
}