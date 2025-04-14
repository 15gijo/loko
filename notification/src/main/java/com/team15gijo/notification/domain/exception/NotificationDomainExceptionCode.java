package com.team15gijo.notification.domain.exception;

import com.team15gijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationDomainExceptionCode implements ExceptionCode {

    SCRIBE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "SSE 연결 중 문제가 발생했습니다."),
    INVALID_USER(HttpStatus.UNAUTHORIZED, " 권한이 없는 사용자입니다. 로그인 후 요청해주세요"),
    INVALID_NOTIFICATION(HttpStatus.BAD_REQUEST, "유효하지 않은 알림입니다."),
    NOT_EXIST(HttpStatus.NOT_FOUND, "알림이 존재하지 않습니다"),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB에서 알림 조회에 실패했습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
