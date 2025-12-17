package com.slowflow.slowflowbackend.history.service;

import com.slowflow.slowflowbackend.history.dto.*;
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

    // 주간 히스토리 조회
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

    // 월간 히스토리 조회
    public MonthlyHistoryResponse getMonthlyHistory(Member member, LocalDate baseDate) {
        LocalDate today = LocalDate.now();
        LocalDate target = (baseDate != null) ? baseDate : today;

        LocalDate monthStart = target.withDayOfMonth(1);
        LocalDate monthEnd = target.withDayOfMonth(target.lengthOfMonth());

        // 해당 월 DailyScore 로딩
        List<DailyScore> scores = dailyScoreRepository.findByMemberIdAndDateBetween(
                member.getId(), monthStart, monthEnd
        );

        Map<LocalDate, DailyScore> byDate = new HashMap<>();
        for (DailyScore ds : scores) byDate.put(ds.getDate(), ds);

        // 해당 월 모든 날짜 총점
        List<MonthlyDayDto> days = new ArrayList<>();
        int goalAchievedDays = 0;

        // 주차별 (+/-) 합
        // key=weekOfMonth, value=[posSum, negSum]
        Map<Integer, int[]> weekSums = new HashMap<>();

        LocalDate cur = monthStart;
        while (!cur.isAfter(monthEnd)) {
            DailyScore ds = byDate.get(cur);

            int total = (ds != null) ? ds.getTotalScore() : 0;
            days.add(new MonthlyDayDto(cur, total));

            if (ds != null && isGoalAchieved(member, ds)) {
                goalAchievedDays++;
            }

            int weekOfMonth = calcWeekOfMonthSundayStart(cur);

            int pos = 0;
            int neg = 0;
            if (ds != null) {
                pos = safe(ds.getDietPositive()) + safe(ds.getExercisePositive()) + safe(ds.getSleepPositive());
                neg = safe(ds.getDietNegative()) + safe(ds.getExerciseNegative()) + safe(ds.getSleepNegative());
            }

            weekSums.putIfAbsent(weekOfMonth, new int[]{0, 0});
            weekSums.get(weekOfMonth)[0] += pos;
            weekSums.get(weekOfMonth)[1] += neg;

            cur = cur.plusDays(1);
        }

        int maxWeek = calcWeekOfMonthSundayStart(monthEnd);
        List<MonthlyWeekDto> weeks = new ArrayList<>();
        for (int w = 1; w <= maxWeek; w++) {
            int[] sum = weekSums.getOrDefault(w, new int[]{0, 0});
            weeks.add(new MonthlyWeekDto(w, sum[0], sum[1]));
        }

        return new MonthlyHistoryResponse(
                target.getYear(),
                target.getMonthValue(),
                monthStart,
                monthEnd,
                goalAchievedDays,
                days,
                weeks
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