package com.slowflow.slowflowbackend.global.config.scoring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class ScoringClientConfig {

    @Bean
    public RestClient scoringRestClient(
            RestClient.Builder builder,
            @Value("${scoring.base-url}") String baseUrl,
            @Value("${scoring.timeout-ms:3000}") int timeoutMs
    ) {
        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout(timeoutMs);
        rf.setReadTimeout(timeoutMs);

        return builder
                .baseUrl(baseUrl)
                .requestFactory(rf)
                .build();
    }
}