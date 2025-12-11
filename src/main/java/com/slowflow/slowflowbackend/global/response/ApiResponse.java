package com.slowflow.slowflowbackend.global.response;

import com.slowflow.slowflowbackend.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private String code;
    private String message;
    private boolean success;
    private T data;

    // 성공
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("OK", "요청이 성공했습니다.", true, data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>("OK", message, true, data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>("CREATED", message, true, data);
    }

    // 실패
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), false, null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customMessage) {
        return new ApiResponse<>(errorCode.getCode(), customMessage, false, null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, T data) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), false, data);
    }
}
