package com.slowflow.slowflowbackend.member.controller;

import com.slowflow.slowflowbackend.global.response.ApiResponse;
import com.slowflow.slowflowbackend.member.dto.ProfileResponse;
import com.slowflow.slowflowbackend.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "프로필 조회 성공",
                        new ProfileResponse(member.getUsername())
                )
        );
    }
}