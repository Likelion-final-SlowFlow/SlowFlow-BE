package com.slowflow.slowflowbackend.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class WeeklyHistoryResponse {

    private int month;               // 몇 월
    private int weekOfMonth;         // 몇 주차 (달력 기준)
    private LocalDate weekStart;     // 주 시작(일)
    private LocalDate weekEnd;       // 주 끝(토)

    private boolean canGoPrev;       // 왼쪽(전주) 이동 가능
    private boolean canGoNext;       // 오른쪽(다음주) 이동 가능 (오늘이 속한 주면 false)

    private int goalAchievedDays;    // 7일 중 목표 달성 일수

    private List<WeeklyDayDto> days; // 항상 7개(일~토)
}