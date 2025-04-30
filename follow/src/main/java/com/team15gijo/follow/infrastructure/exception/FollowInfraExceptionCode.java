package com.team15gijo.follow.infrastructure.exception;

import com.team15gijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FollowInfraExceptionCode implements ExceptionCode {
    KAFKA_JSON_SERIALIZATION_FAILED(HttpStatus.BAD_REQUEST, "json 직렬화 실패입니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
