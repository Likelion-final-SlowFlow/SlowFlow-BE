package com.slowflow.slowflowbackend.report.service;

import com.slowflow.slowflowbackend.global.exception.BaseException;
import com.slowflow.slowflowbackend.global.exception.ErrorCode;
import com.slowflow.slowflowbackend.report.dto.DailyReportAiRequest;
import com.slowflow.slowflowbackend.report.dto.DailyReportAiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ReportAiClient {

    private final RestClient scoringRestClient;

    public String getDailyComment(DailyReportAiRequest request) {
        try {
            return scoringRestClient.post()
                    .uri("/daily-report")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(DailyReportAiResponse.class)
                    .getAiComment();
        } catch (Exception e) {
            throw new BaseException(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "일일 리포트 AI 코멘트 생성 실패"
            );
        }
    }
}