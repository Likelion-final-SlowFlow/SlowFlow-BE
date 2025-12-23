package com.slowflow.slowflowbackend.member.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class GoalUpdateRequest {

    @Min(0)
    @Max(400)
    private int goalScore;
}