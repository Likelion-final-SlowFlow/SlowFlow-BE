package com.slowflow.slowflowbackend.history.service;

import com.slowflow.slowflowbackend.history.dto.WeeklyDayDto;
import com.slowflow.slowflowbackend.history.dto.WeeklyHistoryResponse;
import com.slowflow.slowflowbackend.member.model.Member;
import com.slowflow.slowflowbackend.score.model.DailyScore;
import com.slowflow.slowflowbackend.score.model.DailyState;
import com.slowflow.slowflowbackend.score.repository.DailyScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final DailyScoreRepository dailyScoreRepository;

    public WeeklyHistoryResponse getWeeklyHistory(Member member, LocalDate baseDate) {
        LocalDate today = LocalDate.now();
        LocalDate target = (baseDate != null) ? baseDate : today;

        // 주간 기준: "일~토" (UI와 동일)
        LocalDate weekStart = startOfWeekSunday(target);
        LocalDate weekEnd = weekStart.plusDays(6);

        // "오늘이 속한 주"의 끝(토)보다 뒤는 조회 불가(= 다음주 없음)
        LocalDate currentWeekStart = startOfWeekSunday(today);
        LocalDate currentWeekEnd = currentWeekStart.plusDays(6);

        boolean canGoNext = weekEnd.isBefore(currentWeekEnd); // 오늘이 속한 주보다 과거면 next 가능, 현재 주면 next 불가
        boolean canGoPrev = true; // 항상 전 주로는 이동 가능

        // 해당 주 DailyScore들 로딩
        List<DailyScore> scores = dailyScoreRepository.findByMemberIdAndDateBetween(member.getId(), weekStart, weekEnd);
        Map<LocalDate, DailyScore> byDate = new HashMap<>();
        for (DailyScore ds : scores) byDate.put(ds.getDate(), ds);

        // 7일 고정(days: 일~토)
        List<WeeklyDayDto> days = new ArrayList<>();
        int goalAchievedDays = 0;

        for (int i = 0; i < 7; i++) {
            LocalDate d = weekStart.plusDays(i);
            DailyScore ds = byDate.get(d);

            int totalScore = 0;
            int pos = 0;
            int neg = 0;

            if (ds != null) {
                totalScore = ds.getTotalScore();
                pos = safe(ds.getDietPositive()) + safe(ds.getExercisePositive()) + safe(ds.getSleepPositive());
                neg = safe(ds.getDietNegative()) + safe(ds.getExerciseNegative()) + safe(ds.getSleepNegative());

                if (isGoalAchieved(member, ds)) {
                    goalAchievedDays++;
                }
            }

            days.add(new WeeklyDayDto(d, totalScore, pos, neg));
        }

        int month = target.getMonthValue();
        int weekOfMonth = calcWeekOfMonthSundayStart(target);

        return new WeeklyHistoryResponse(
                month,
                weekOfMonth,
                weekStart,
                weekEnd,
                canGoPrev,
                canGoNext,
                goalAchievedDays,
                days
        );
    }

    // ---------- helpers ----------

    private static int safe(Integer v) {
        return (v == null) ? 0 : v;
    }

    private static LocalDate startOfWeekSunday(LocalDate date) {
        // DayOfWeek: MON=1 ... SUN=7
        int dow = date.getDayOfWeek().getValue() % 7; // SUN -> 0, MON -> 1 ...
        return date.minusDays(dow);
    }

    // "몇월 몇주차" 계산(주 시작=일요일)
    private static int calcWeekOfMonthSundayStart(LocalDate date) {
        LocalDate first = date.withDayOfMonth(1);
        int offset = first.getDayOfWeek().getValue() % 7; // SUN 0 ~ SAT 6
        return ((date.getDayOfMonth() + offset - 1) / 7) + 1;
    }

    private static boolean isGoalAchieved(Member member, DailyScore ds) {
        // 1) state가 GOAL_REACHED면 바로 true
        if (ds.getState() == DailyState.GOAL_REACHED) return true;

        int goal = member.getGoalScore();
        return goal > 0 && ds.getTotalScore() >= goal;
    }
}