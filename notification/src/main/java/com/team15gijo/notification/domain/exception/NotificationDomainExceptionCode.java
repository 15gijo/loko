package com.team15gijo.notification.domain.exception;

import com.team15gijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationDomainExceptionCode implements ExceptionCode {

    INVALID_NOTIFICATION(HttpStatus.BAD_REQUEST, "유효하지 않은 알림입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
