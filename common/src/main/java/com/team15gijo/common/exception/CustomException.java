package com.team15gijo.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public CustomException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    // 원인 있는 에러
    public CustomException(ExceptionCode exceptionCode, Throwable cause) {
        super(exceptionCode.getMessage(), cause);
        this.exceptionCode = exceptionCode;
    }
}
