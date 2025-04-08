package com.team15gijo.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공통 API 응답 포맷 클래스
 * <p>
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAIL = "FAIL";

    private String status;
    private String message;
    private T data;

    //성공
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(STATUS_SUCCESS, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(STATUS_SUCCESS, message, null);
    }

    //실패
    public static <T> ApiResponse<T> exception(String message, T errorCode) {
        return new ApiResponse<>(STATUS_FAIL, message, errorCode);
    }
}
