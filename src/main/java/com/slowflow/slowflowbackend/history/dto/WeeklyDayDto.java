package com.slowflow.slowflowbackend.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class WeeklyDayDto {
    private LocalDate date;        // 해당 날짜
    private int totalScore;        // 그날 총점
    private int positiveTotal;     // 그날 총 양의 점수 합
    private int negativeTotal;     // 그날 총 음의 점수 합(절대값)
}