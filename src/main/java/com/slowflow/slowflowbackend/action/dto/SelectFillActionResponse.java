package com.slowflow.slowflowbackend.action.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SelectFillActionResponse {

    private LocalDate date;
    private int appliedScore;     // 실제 반영된 점수(캡 때문에 줄어들 수 있음)
    private int updatedTotalScore;
}