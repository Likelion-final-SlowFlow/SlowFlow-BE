package com.slowflow.slowflowbackend.action.dto;

import com.slowflow.slowflowbackend.rule.model.RuleCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateActionRequest {

    @NotNull(message = "category는 필수입니다.")
    private RuleCategory category;

    @NotBlank(message = "text는 필수입니다.")
    private String text;
}