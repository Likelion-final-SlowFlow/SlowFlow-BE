package com.slowflow.slowflowbackend.action.service;

import com.slowflow.slowflowbackend.action.dto.*;
import com.slowflow.slowflowbackend.action.model.FillAction;
import com.slowflow.slowflowbackend.action.model.UserAction;
import com.slowflow.slowflowbackend.action.repository.FillActionRepository;
import com.slowflow.slowflowbackend.action.repository.UserActionRepository;
import com.slowflow.slowflowbackend.global.exception.BaseException;
import com.slowflow.slowflowbackend.global.exception.ErrorCode;
import com.slowflow.slowflowbackend.member.model.Member;
import com.slowflow.slowflowbackend.rule.model.RuleCategory;
import com.slowflow.slowflowbackend.score.model.DailyScore;
import com.slowflow.slowflowbackend.score.model.DailyState;
import com.slowflow.slowflowbackend.score.repository.DailyScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FillActionService {

    private final FillActionRepository fillActionRepository;
    private final DailyScoreRepository dailyScoreRepository;
    private final UserActionRepository userActionRepository;

    // ===== CAP 상수 =====
    private static final int DIET_POS_CAP = 100;
    private static final int DIET_NEG_CAP = 160;

    private static final int EX_POS_CAP = 120;
    private static final int EX_NEG_CAP = 40;

    private static final int SLEEP_POS_CAP = 80;
    private static final int SLEEP_NEG_CAP = 120;

    private static final int TOTAL_MIN = -300;
    private static final int TOTAL_MAX = 300;

    // ----------------------------
    // 1) 바텀 시트 조회
    // ----------------------------
    public FillActionBottomSheetResponse getBottomSheet(Member member) {
        LocalDate today = LocalDate.now();

        DailyScore todayScore = dailyScoreRepository
                .findByMemberIdAndDate(member.getId(), today)
                .orElse(null);

        int currentTotal = (todayScore == null) ? 0 : todayScore.getTotalScore();
        boolean recommendable = currentTotal < 0;

        List<FillActionDto> items = List.of();
        if (recommendable) {
            items = fillActionRepository.findRandomTop3Positive()
                    .stream()
                    .map(f -> new FillActionDto(f.getId(), f.getCategory(), f.getBehavior(), f.getScore()))
                    .toList();
        }

        return new FillActionBottomSheetResponse(today, currentTotal, recommendable, items);
    }

    // ----------------------------
    // 2) 추천 행동 선택 → 오늘 행동 추가 + 점수 반영
    // ----------------------------
    public SelectFillActionResponse selectFillAction(Member member, Long fillActionId) {
        LocalDate today = LocalDate.now();

        DailyScore ds = dailyScoreRepository
                .findByMemberIdAndDate(member.getId(), today)
                .orElseGet(() -> dailyScoreRepository.save(
                        DailyScore.builder()
                                .member(member)
                                .date(today)
                                .dietPositive(0).dietNegative(0)
                                .exercisePositive(0).exerciseNegative(0)
                                .sleepPositive(0).sleepNegative(0)
                                .totalScore(0)
                                .state(DailyState.NONE)
                                .build()
                ));

        // 요구사항: 현재 점수가 음수일 때만 추천 행동 선택 가능
        if (ds.getTotalScore() >= 0) {
            throw new BaseException(ErrorCode.FILL_ACTION_NOT_AVAILABLE); // 새 에러코드 추가 권장
        }

        FillAction fillAction = fillActionRepository.findById(fillActionId)
                .orElseThrow(() -> new BaseException(ErrorCode.FILL_ACTION_NOT_FOUND)); // 새 에러코드 추가 권장

        int applied = applyToDailyScore(member, ds, fillAction.getCategory(), fillAction.getScore());
        dailyScoreRepository.save(ds);

        // UserAction 기록 추가
        UserAction ua = UserAction.builder()
                .member(member)
                .category(fillAction.getCategory())
                .rawText("[추천 행동] " + fillAction.getBehavior())
                .parsedJson(null)
                .score(applied)
                .reason("바텀시트 추천 행동 선택")
                .date(today)
                .createdAt(LocalDateTime.now())
                .rule(null) // 추천 행동은 rule 연결이 필요 없으면 null
                .build();

        userActionRepository.save(ua);

        return new SelectFillActionResponse(today, applied, ds.getTotalScore());
    }

    // ===== 내부 로직 =====

    private int applyToDailyScore(Member member, DailyScore ds, RuleCategory category, int score) {
        if (score == 0) return 0;

        if (category == RuleCategory.DIET) {
            return applyCategory(ds, score, DIET_POS_CAP, DIET_NEG_CAP,
                    ds.getDietPositive(), ds.getDietNegative(),
                    ds::updateDietPositive, ds::updateDietNegative, member);
        }

        if (category == RuleCategory.EXERCISE) {
            return applyCategory(ds, score, EX_POS_CAP, EX_NEG_CAP,
                    ds.getExercisePositive(), ds.getExerciseNegative(),
                    ds::updateExercisePositive, ds::updateExerciseNegative, member);
        }

        if (category == RuleCategory.SLEEP) {
            return applyCategory(ds, score, SLEEP_POS_CAP, SLEEP_NEG_CAP,
                    ds.getSleepPositive(), ds.getSleepNegative(),
                    ds::updateSleepPositive, ds::updateSleepNegative, member);
        }

        // 3개 카테고리만 허용
        throw new BaseException(ErrorCode.INVALID_CATEGORY);
    }

    @FunctionalInterface
    private interface IntSetter { void set(int v); }

    private int applyCategory(
            DailyScore ds,
            int score,
            int posCap,
            int negCap,
            int currentPos,
            int currentNeg,
            IntSetter setPos,
            IntSetter setNeg,
            Member member
    ) {
        int applied;

        if (score > 0) {
            int remain = Math.max(0, posCap - currentPos);
            applied = Math.min(score, remain);
            if (applied <= 0) throw new BaseException(ErrorCode.DAILY_CAP_REACHED);
            setPos.set(currentPos + applied);
        } else {
            int abs = Math.abs(score);
            int remain = Math.max(0, negCap - currentNeg);
            int add = Math.min(abs, remain);
            if (add <= 0) throw new BaseException(ErrorCode.DAILY_CAP_REACHED);
            setNeg.set(currentNeg + add);
            applied = -add;
        }

        // total 재계산 + clamp(-300~300)
        int posSum = safe(ds.getDietPositive()) + safe(ds.getExercisePositive()) + safe(ds.getSleepPositive());
        int negSum = safe(ds.getDietNegative()) + safe(ds.getExerciseNegative()) + safe(ds.getSleepNegative());

        int total = posSum - negSum;
        total = clamp(total, TOTAL_MIN, TOTAL_MAX);
        ds.updateTotalScore(total);

        // state 업데이트
        ds.updateState(calcState(member, total));

        return applied;
    }

    private static int safe(Integer v) { return v == null ? 0 : v; }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static DailyState calcState(Member member, int totalScore) {
        int goal = member.getGoalScore();

        if (goal > 0 && totalScore >= goal) return DailyState.GOAL_REACHED;
        if (totalScore > 0) return DailyState.POSITIVE;
        if (totalScore < 0) return DailyState.NEGATIVE;
        return DailyState.NEUTRAL;
    }
}