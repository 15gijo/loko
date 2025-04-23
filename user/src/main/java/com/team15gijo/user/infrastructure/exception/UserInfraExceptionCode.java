package com.team15gijo.user.infrastructure.exception;

import com.team15gijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserInfraExceptionCode implements ExceptionCode {
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 주소입니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
