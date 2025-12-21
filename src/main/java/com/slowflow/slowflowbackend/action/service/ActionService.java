package com.slowflow.slowflowbackend.action.service;

import com.slowflow.slowflowbackend.action.dto.CreateActionRequest;
import com.slowflow.slowflowbackend.action.dto.CreateActionResponse;
import com.slowflow.slowflowbackend.action.model.UserAction;
import com.slowflow.slowflowbackend.action.repository.UserActionRepository;
import com.slowflow.slowflowbackend.global.exception.BaseException;
import com.slowflow.slowflowbackend.global.exception.ErrorCode;
import com.slowflow.slowflowbackend.member.model.Member;
import com.slowflow.slowflowbackend.rule.model.RuleCategory;
import com.slowflow.slowflowbackend.scoring.client.ScoringClient;
import com.slowflow.slowflowbackend.scoring.dto.ScoringRequest;
import com.slowflow.slowflowbackend.scoring.dto.ScoringResponse;
import com.slowflow.slowflowbackend.score.model.DailyScore;
import com.slowflow.slowflowbackend.score.model.DailyState;
import com.slowflow.slowflowbackend.score.repository.DailyScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ActionService {

    private final ScoringClient scoringClient;
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

    public CreateActionResponse createAction(Member member, CreateActionRequest req) {
        LocalDate today = LocalDate.now();

        // 1) 오늘 DailyScore 없으면 생성
        DailyScore ds = dailyScoreRepository.findByMemberIdAndDate(member.getId(), today)
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

        // 2) FastAPI 채점 요청
        ScoringResponse scored = scoringClient.score(new ScoringRequest(req.getCategory(), req.getText()));
        int rawScore = scored.getScore();
        String reason = scored.getReason();

        // 3) Spring에서 CAP/CLAMP 반영하여 실제 적용 점수 계산 + DailyScore 업데이트
        int appliedScore = applyToDailyScore(member, ds, req.getCategory(), rawScore);
        dailyScoreRepository.save(ds);

        // 4) UserAction 저장
        UserAction ua = UserAction.builder()
                .member(member)
                .category(req.getCategory())
                .rawText(req.getText())
                .parsedJson(null)          // 필요하면 FastAPI 결과를 JSON으로 넣어도 됨
                .score(appliedScore)       // “실제 적용된 점수” 저장
                .reason(reason)
                .date(today)
                .createdAt(LocalDateTime.now())
                .rule(null)                // 지금은 ScoringRule 연결 안 쓰는 구조면 null
                .build();
        userActionRepository.save(ua);

        return new CreateActionResponse(
                today,
                rawScore,
                appliedScore,
                ds.getTotalScore(),
                ds.getState(),
                reason
        );
    }

    // ===== 아래는 FillActionService와 동일한 점수 반영 로직 =====

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

        throw new BaseException(ErrorCode.INVALID_INPUT, "허용되지 않은 카테고리입니다.");
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

        ds.updateState(calcState(member, total));
        return applied;
    }

    private static int safe(Integer v) { return v == null ? 0 : v; }
    private static int clamp(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }

    private static DailyState calcState(Member member, int totalScore) {
        int goal = member.getGoalScore();
        if (goal > 0 && totalScore >= goal) return DailyState.GOAL_REACHED;
        if (totalScore > 0) return DailyState.POSITIVE;
        if (totalScore < 0) return DailyState.NEGATIVE;
        return DailyState.NEUTRAL;
    }
}