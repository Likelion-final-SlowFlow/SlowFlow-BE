package com.slowflow.slowflowbackend.report.service;

import com.slowflow.slowflowbackend.action.model.UserAction;
import com.slowflow.slowflowbackend.action.repository.UserActionRepository;
import com.slowflow.slowflowbackend.member.model.Member;
import com.slowflow.slowflowbackend.report.dto.*;
import com.slowflow.slowflowbackend.rule.model.RuleCategory;
import com.slowflow.slowflowbackend.score.model.DailyScore;
import com.slowflow.slowflowbackend.score.repository.DailyScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyReportService {

    private final UserActionRepository userActionRepository;
    private final DailyScoreRepository dailyScoreRepository;
    private final ReportAiClient reportAiClient;

    public DailyReportResponse getDailyReport(Member member, LocalDate date) {

        List<UserAction> actions =
                userActionRepository.findByMemberIdAndDate(member.getId(), date);

        DailyScore ds = dailyScoreRepository
                .findByMemberIdAndDate(member.getId(), date)
                .orElse(null);

        int totalPositive = 0;
        int totalNegative = 0;
        int totalScore = (ds != null) ? ds.getTotalScore() : 0;

        if (ds != null) {
            totalPositive =
                    ds.getDietPositive() +
                            ds.getExercisePositive() +
                            ds.getSleepPositive();

            totalNegative =
                    ds.getDietNegative() +
                            ds.getExerciseNegative() +
                            ds.getSleepNegative();
        }

        // 카테고리별 그룹핑
        Map<RuleCategory, List<UserAction>> grouped =
                actions.stream().collect(Collectors.groupingBy(UserAction::getCategory));

        List<DailyReportResponse.CategoryReport> categories = new ArrayList<>();

        for (RuleCategory category : RuleCategory.values()) {
            List<UserAction> list = grouped.getOrDefault(category, List.of());

            int categoryTotal = list.stream().mapToInt(UserAction::getScore).sum();

            List<DailyReportResponse.ActionItem> items = list.stream()
                    .map(a -> new DailyReportResponse.ActionItem(a.getRawText(), a.getScore()))
                    .toList();

            categories.add(new DailyReportResponse.CategoryReport(
                    category,
                    categoryTotal,
                    items
            ));
        }

        // FastAPI 요청
        DailyReportAiRequest aiReq = buildAiRequest(date, categories);
        String aiComment = reportAiClient.getDailyComment(aiReq);

        return new DailyReportResponse(
                date,
                totalPositive,
                totalNegative,
                totalScore,
                categories,
                aiComment
        );
    }

    private DailyReportAiRequest buildAiRequest(
            LocalDate date,
            List<DailyReportResponse.CategoryReport> categories
    ) {
        List<DailyReportAiRequest.Category> aiCategories = categories.stream()
                .map(c -> new DailyReportAiRequest.Category(
                        c.getCategory(),
                        c.getTotalScore(),
                        c.getActions().stream()
                                .map(a -> new DailyReportAiRequest.Action(a.getText(), a.getScore()))
                                .toList()
                ))
                .toList();

        return new DailyReportAiRequest(date, aiCategories);
    }
}