package com.slowflow.slowflowbackend.report.dto;

import com.slowflow.slowflowbackend.rule.model.RuleCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class DailyReportResponse {

    private LocalDate date;

    private int totalPositive;
    private int totalNegative;
    private int totalScore;

    private List<CategoryReport> categories;

    private String aiComment;

    @Getter
    @AllArgsConstructor
    public static class CategoryReport {
        private RuleCategory category;
        private int totalScore;
        private List<ActionItem> actions;
    }

    @Getter
    @AllArgsConstructor
    public static class ActionItem {
        private String text;
        private int score;
    }
}