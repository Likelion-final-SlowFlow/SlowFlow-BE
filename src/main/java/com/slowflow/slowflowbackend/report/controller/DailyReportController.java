package com.slowflow.slowflowbackend.report.controller;

import com.slowflow.slowflowbackend.global.response.ApiResponse;
import com.slowflow.slowflowbackend.member.model.Member;
import com.slowflow.slowflowbackend.report.dto.DailyReportResponse;
import com.slowflow.slowflowbackend.report.service.DailyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class DailyReportController {

    private final DailyReportService dailyReportService;

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailyReportResponse>> getDailyReport(
            Authentication authentication,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        Member member = (Member) authentication.getPrincipal();
        LocalDate target = (date != null) ? date : LocalDate.now();

        DailyReportResponse res = dailyReportService.getDailyReport(member, target);
        return ResponseEntity.ok(ApiResponse.ok("일일 리포트 조회 성공", res));
    }
}