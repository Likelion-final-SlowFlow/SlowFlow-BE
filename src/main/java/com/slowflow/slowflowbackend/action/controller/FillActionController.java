package com.slowflow.slowflowbackend.action.controller;

import com.slowflow.slowflowbackend.action.dto.FillActionBottomSheetResponse;
import com.slowflow.slowflowbackend.action.dto.SelectFillActionResponse;
import com.slowflow.slowflowbackend.action.service.FillActionService;
import com.slowflow.slowflowbackend.global.response.ApiResponse;
import com.slowflow.slowflowbackend.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fill-actions")
public class FillActionController {

    private final FillActionService fillActionService;

    // 바텀 시트 조회
    @GetMapping("/bottom-sheet")
    public ResponseEntity<ApiResponse<FillActionBottomSheetResponse>> getBottomSheet(Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        FillActionBottomSheetResponse res = fillActionService.getBottomSheet(member);
        return ResponseEntity.ok(ApiResponse.ok("바텀시트 추천 행동 조회 성공", res));
    }

    // 추천 행동 선택
    @PostMapping("/{fillActionId}/select")
    public ResponseEntity<ApiResponse<SelectFillActionResponse>> select(
            Authentication authentication,
            @PathVariable Long fillActionId
    ) {
        Member member = (Member) authentication.getPrincipal();
        SelectFillActionResponse res = fillActionService.selectFillAction(member, fillActionId);
        return ResponseEntity.ok(ApiResponse.ok("추천 행동 선택 성공", res));
    }
}