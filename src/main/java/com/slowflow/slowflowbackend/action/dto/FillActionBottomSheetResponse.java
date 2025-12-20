package com.slowflow.slowflowbackend.action.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class FillActionBottomSheetResponse {

    private LocalDate date;          // 오늘 날짜
    private int currentScore;         // 오늘 totalScore
    private boolean recommendable;    // currentScore < 0 이면 true
    private List<FillActionDto> items;
}