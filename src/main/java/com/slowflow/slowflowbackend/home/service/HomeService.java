package com.slowflow.slowflowbackend.home.service;

import com.slowflow.slowflowbackend.action.repository.UserActionRepository;
import com.slowflow.slowflowbackend.feedback.model.Feedback;
import com.slowflow.slowflowbackend.feedback.model.FeedbackType;
import com.slowflow.slowflowbackend.feedback.repository.FeedbackRepository;
import com.slowflow.slowflowbackend.home.dto.HomeDashboardResponse;
import com.slowflow.slowflowbackend.member.model.Member;
import com.slowflow.slowflowbackend.score.model.DailyScore;
import com.slowflow.slowflowbackend.score.repository.DailyScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final DailyScoreRepository dailyScoreRepository;
    private final UserActionRepository userActionRepository;
    private final FeedbackRepository feedbackRepository;

    public HomeDashboardResponse getDashboard(Member member) {
        LocalDate today = LocalDate.now();

        DailyScore todayScore = dailyScoreRepository
                .findByMemberIdAndDate(member.getId(), today)
                .orElse(null);

        boolean hasTodayAction = userActionRepository.existsByMemberIdAndDate(member.getId(), today);

        int currentScore = (todayScore != null) ? todayScore.getTotalScore() : 0;

        int goalScore = member.getGoalScore();
        if (goalScore < 0) goalScore = 0;

        int remaining = Math.max(goalScore - currentScore, 0);

        FeedbackType type = decideFeedbackType(goalScore, currentScore, todayScore, hasTodayAction);

        String feedback = feedbackRepository.findRandomByType(type)
                .map(Feedback::getMessage)
                .orElseGet(() -> fallbackMessage(type));

        return new HomeDashboardResponse(
                today,
                currentScore,
                goalScore,
                remaining,
                feedback
        );
    }

    private FeedbackType decideFeedbackType(int goalScore,
                                            int currentScore,
                                            DailyScore todayScore,
                                            boolean hasTodayAction) {

        // 케이스 5: 목표 달성 (점수 >= 목표)
        if (goalScore > 0 && currentScore >= goalScore) {
            return FeedbackType.GOAL_REACHED;
        }

        // 케이스 1: 시작 전(기록 없음)
        // - DailyScore도 없고, UserAction도 없으면 기록 없음
        if (todayScore == null && !hasTodayAction) {
            return FeedbackType.START;
        }

        // 케이스 2: 0점(기록은 있는데 상쇄됨)
        if (currentScore == 0) {
            return FeedbackType.ZERO;
        }

        // 케이스 3/4
        return (currentScore > 0) ? FeedbackType.POSITIVE : FeedbackType.NEGATIVE;
    }

    private String fallbackMessage(FeedbackType type) {
        // DB에 문구가 없을 때 최소 안전문구
        return switch (type) {
            case START -> "오늘 하루를 시작해볼까요?<br>첫 기록을 남겨보세요!";
            case ZERO -> "딱 균형이에요!<br>조금만 더 채워보면 플러스!";
            case POSITIVE -> "좋은 흐름이에요!<br>이 페이스로 계속 가봐요!";
            case NEGATIVE -> "부담이 조금 더 많아요<br>작은 채움으로 회복해봐요";
            case GOAL_REACHED -> "오늘 목표 완수!<br>내일도 이렇게 채워가봐요";
        };
    }
}