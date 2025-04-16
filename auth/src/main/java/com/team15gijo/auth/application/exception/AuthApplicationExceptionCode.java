package com.team15gijo.auth.application.exception;

import com.team15gijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthApplicationExceptionCode implements ExceptionCode {

    USER_SERVICE_INFO_FAILED(HttpStatus.BAD_REQUEST, "유저 서버 정보 조회 실패");

    private final HttpStatus httpStatus;
    private final String message;
}
