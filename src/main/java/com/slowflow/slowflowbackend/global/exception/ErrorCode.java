package com.slowflow.slowflowbackend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 공통
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "입력값이 유효하지 않습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "입력값이 올바르지 않습니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", "필수 파라미터가 누락되었습니다."),
    INVALID_JSON(HttpStatus.BAD_REQUEST, "INVALID_JSON", "JSON 형식이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다."),

    // 인증/권한
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", "이미 사용 중인 이메일입니다."),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "USERNAME_ALREADY_EXISTS", "이미 사용 중인 아이디입니다."),
    PASSWORDS_DO_NOT_MATCH(HttpStatus.BAD_REQUEST, "PASSWORDS_DO_NOT_MATCH", "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 사용자입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "INVALID_PASSWORD", "비밀번호가 일치하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "리프레시 토큰이 유효하지 않습니다."),

    FILL_ACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "FILL_ACTION_NOT_FOUND", "추천 행동을 찾을 수 없습니다."),
    FILL_ACTION_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "FILL_ACTION_NOT_AVAILABLE", "현재 점수가 음수일 때만 추천 행동을 선택할 수 있습니다."),
    DAILY_CAP_REACHED(HttpStatus.CONFLICT, "DAILY_CAP_REACHED", "오늘 해당 카테고리 점수 한도(CAP)에 도달했습니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "INVALID_CATEGORY", "유효하지 않은 카테고리입니다."),
    // 최종 안전망
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status; // HTTP 상태 코드
    private final String code;       // 에러 식별 코드
    private final String message;    // 기본 메시지
}
