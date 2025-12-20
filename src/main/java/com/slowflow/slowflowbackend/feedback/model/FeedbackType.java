package com.slowflow.slowflowbackend.feedback.model;

public enum FeedbackType {
    START,        // 기록 없음
    ZERO,         // 0점(기록은 있는데 상쇄됨)
    POSITIVE,     // 점수 > 0
    NEGATIVE,     // 점수 < 0
    GOAL_REACHED  // 점수 >= 목표
}