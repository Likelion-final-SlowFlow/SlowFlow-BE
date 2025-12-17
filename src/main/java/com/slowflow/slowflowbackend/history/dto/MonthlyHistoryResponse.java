package com.slowflow.slowflowbackend.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class MonthlyHistoryResponse {
    private int year;                 // 몇년
    private int month;                // 몇월
    private LocalDate monthStart;
    private LocalDate monthEnd;

    private int goalAchievedDays;     // 30/31일 중 목표 달성한 일수

    private List<MonthlyDayDto> days;     // 해당 월 모든 날짜의 총점
    private List<MonthlyWeekDto> weeks;   // 주차별 (+/-) 합계
}