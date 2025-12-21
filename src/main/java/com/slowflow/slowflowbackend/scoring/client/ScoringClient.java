package com.slowflow.slowflowbackend.scoring.client;

import com.slowflow.slowflowbackend.global.exception.BaseException;
import com.slowflow.slowflowbackend.global.exception.ErrorCode;
import com.slowflow.slowflowbackend.scoring.dto.ScoringRequest;
import com.slowflow.slowflowbackend.scoring.dto.ScoringResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class ScoringClient {

    private final RestClient scoringRestClient;

    @Value("${scoring.internal-token:}")
    private String internalToken;

    public ScoringResponse score(ScoringRequest request) {
        try {
            RestClient.RequestBodySpec spec = scoringRestClient.post()
                    .uri("/scoring")
                    .contentType(MediaType.APPLICATION_JSON);

            if (internalToken != null && !internalToken.isBlank()) {
                spec = spec.header("X-Internal-Token", internalToken);
            }

            ScoringResponse body = spec
                    .body(request)
                    .retrieve()
                    .body(ScoringResponse.class);

            if (body == null) {
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, "채점 서버 응답 바디가 비었습니다.");
            }
            return body;

        } catch (RestClientException e) {
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, "채점 서버 호출 실패: " + e.getMessage());
        }
    }
}