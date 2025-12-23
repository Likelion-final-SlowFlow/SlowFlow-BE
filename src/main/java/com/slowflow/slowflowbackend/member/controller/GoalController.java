package com.slowflow.slowflowbackend.member.controller;

import com.slowflow.slowflowbackend.global.response.ApiResponse;
import com.slowflow.slowflowbackend.member.dto.GoalUpdateRequest;
import com.slowflow.slowflowbackend.member.model.Member;
import com.slowflow.slowflowbackend.member.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile/goal")
public class GoalController {

    private final GoalService goalService;

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> updateGoal(
            Authentication authentication,
            @RequestBody @Valid GoalUpdateRequest request
    ) {
        Member member = (Member) authentication.getPrincipal();
        goalService.updateGoal(member, request.getGoalScore());

        return ResponseEntity.ok(
                ApiResponse.ok("목표 점수가 설정되었습니다.", null)
        );
    }
}