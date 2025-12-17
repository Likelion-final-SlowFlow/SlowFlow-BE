package com.slowflow.slowflowbackend.history.controller;

import com.slowflow.slowflowbackend.global.response.ApiResponse;
import com.slowflow.slowflowbackend.history.dto.MonthlyHistoryResponse;
import com.slowflow.slowflowbackend.history.dto.WeeklyHistoryResponse;
import com.slowflow.slowflowbackend.history.service.HistoryService;
import com.slowflow.slowflowbackend.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
public class HistoryController {

    private final HistoryService historyService;

    // 주간 히스토리 조회
    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<WeeklyHistoryResponse>> getWeekly(
            Authentication authentication,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate baseDate
    ) {
        Member member = (Member) authentication.getPrincipal();

        WeeklyHistoryResponse result = historyService.getWeeklyHistory(member, baseDate);
        return ResponseEntity.ok(ApiResponse.ok("주간 히스토리 조회 성공", result));
    }

    // 월간 히스토리 조회
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<MonthlyHistoryResponse>> getMonthly(
            Authentication authentication,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate baseDate
    ) {
        Member member = (Member) authentication.getPrincipal();
        MonthlyHistoryResponse result = historyService.getMonthlyHistory(member, baseDate);
        return ResponseEntity.ok(ApiResponse.ok("월간 히스토리 조회 성공", result));
    }
}