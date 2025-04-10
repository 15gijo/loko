package com.team15gijo.auth.domain.exception;

import com.team15gijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthDomainExceptionCode implements ExceptionCode {
    ROLE_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "유효 하지 않은 롤 입니다."),
    LOGIN_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "유효 하지 않은 로그인 타입 입니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
