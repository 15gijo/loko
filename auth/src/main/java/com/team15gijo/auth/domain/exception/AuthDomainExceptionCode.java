package com.team15gijo.auth.domain.exception;

import com.team15gijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthDomainExceptionCode implements ExceptionCode {
    ROLE_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "유효 하지 않은 롤 입니다."),
    LOGIN_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "유효 하지 않은 로그인 타입 입니다."),
    USER_IDENTIFIER_NOT_FOUND(HttpStatus.NOT_FOUND, "유효 하지 않은 아이디 입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치 하지 않습니다."),
    AUTH_IS_DUPLICATED(HttpStatus.CONFLICT, "이미 등록된 인증 정보가 존재합니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
