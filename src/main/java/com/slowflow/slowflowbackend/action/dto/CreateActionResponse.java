package com.slowflow.slowflowbackend.action.dto;

import com.slowflow.slowflowbackend.score.model.DailyState;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CreateActionResponse {
    private LocalDate date;
    private int rawScore;          // FastAPI가 준 원점수
    private int appliedScore;      // CAP 반영 후 실제 적용 점수
    private int updatedTotalScore; // 반영 후 오늘 총점
    private DailyState state;      // 반영 후 상태
    private String reason;         // 채점 근거(설명)
}