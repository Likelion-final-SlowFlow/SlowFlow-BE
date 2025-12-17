package com.slowflow.slowflowbackend.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyWeekDto {
    private int weekOfMonth;      // 1주차~4/5(6)주차
    private int positiveTotal;    // 해당 주차의 총 양수 점수 합
    private int negativeTotal;    // 해당 주차의 총 음수 점수 합
}