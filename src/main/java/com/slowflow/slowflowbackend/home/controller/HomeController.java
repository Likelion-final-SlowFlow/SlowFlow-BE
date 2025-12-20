package com.slowflow.slowflowbackend.home.controller;

import com.slowflow.slowflowbackend.global.response.ApiResponse;
import com.slowflow.slowflowbackend.home.dto.HomeDashboardResponse;
import com.slowflow.slowflowbackend.home.service.HomeService;
import com.slowflow.slowflowbackend.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;

    // 홈 대시보드 조회
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<HomeDashboardResponse>> dashboard(Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        HomeDashboardResponse result = homeService.getDashboard(member);

        return ResponseEntity.ok(ApiResponse.ok("홈 대시보드 조회 성공", result));
    }
}