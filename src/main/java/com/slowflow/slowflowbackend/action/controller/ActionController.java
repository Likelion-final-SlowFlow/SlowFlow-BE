package com.slowflow.slowflowbackend.action.controller;

import com.slowflow.slowflowbackend.action.dto.CreateActionRequest;
import com.slowflow.slowflowbackend.action.dto.CreateActionResponse;
import com.slowflow.slowflowbackend.action.service.ActionService;
import com.slowflow.slowflowbackend.global.response.ApiResponse;
import com.slowflow.slowflowbackend.member.model.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/actions")
public class ActionController {

    private final ActionService actionService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateActionResponse>> create(
            Authentication authentication,
            @RequestBody @Valid CreateActionRequest request
    ) {
        Member member = (Member) authentication.getPrincipal();
        CreateActionResponse res = actionService.createAction(member, request);
        return ResponseEntity.ok(ApiResponse.ok("행동 입력 성공", res));
    }
}