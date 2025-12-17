package com.slowflow.slowflowbackend.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MonthlyDayDto {
    private LocalDate date;
    private int totalScore;
}
