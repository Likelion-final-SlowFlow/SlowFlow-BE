package com.slowflow.slowflowbackend.report.dto;

import com.slowflow.slowflowbackend.rule.model.RuleCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class DailyReportAiRequest {

    private LocalDate date;
    private List<Category> categories;

    @Getter
    @AllArgsConstructor
    public static class Category {
        private RuleCategory category;
        private int totalScore;
        private List<Action> actions;
    }

    @Getter
    @AllArgsConstructor
    public static class Action {
        private String text;
        private int score;
    }
}