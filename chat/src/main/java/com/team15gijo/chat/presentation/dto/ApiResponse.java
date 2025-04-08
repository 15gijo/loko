package com.team15gijo.chat.presentation.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {
    private final int status; // HTTP 상태 코드
    private final boolean success; // 성공여부
    private final T body; // 실제 응답 데이터

    // 성공 응답 생성
    public static <T> ApiResponse<T> success(int status, T body) {
        return new ApiResponse<>(status, true, body);
    }

    // 실패 응답 생성
    public static <T> ApiResponse<T> failure(int status, T body) {
        return new ApiResponse<>(status, false, body);
    }
}
