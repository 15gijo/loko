package com.team15gijo.user.application.exception;

import com.team15gijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserApplicationExceptionCode implements ExceptionCode {
    AUTH_SERVICE_SIGNUP_FAILED(HttpStatus.BAD_REQUEST, "인증 서버 회원가입 연동 실패"),
    INVALID_UPDATE_PARAMETER(HttpStatus.BAD_REQUEST, "유효하지 않은 업데이트 요청입니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "유효하지 않은 유저입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
