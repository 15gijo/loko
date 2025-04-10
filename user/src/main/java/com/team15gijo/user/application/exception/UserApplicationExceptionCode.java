package com.team15gijo.user.application.exception;

import com.team15gijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserApplicationExceptionCode implements ExceptionCode {
    AUTH_SERVICE_SIGNUP_FAILED(HttpStatus.BAD_REQUEST, "인증 서버 회원가입 연동 실패");

    private final HttpStatus httpStatus;
    private final String message;
}
