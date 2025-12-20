package com.slowflow.slowflowbackend.home.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class HomeDashboardResponse {

    private LocalDate date;         // 오늘 날짜
    private int currentScore;       // 오늘 점수(00:00 기준으로 새로 시작)
    private int goalScore;          // 사용자 목표 점수
    private int remainingToGoal;    // 목표까지 남은 점수 (0 이상)
    private String feedback;        // 케이스별 랜덤 문구
}