package com.team15gijo.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonExceptionCode implements ExceptionCode {
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 매개변수 입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 문제가 발생되었습니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "유효성 검사에 실패했습니다."),
    PAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 페이지를 찾을 수 없습니다."),
    INVALID_PAGE_NUMBER(HttpStatus.BAD_REQUEST, "잘못된 폐이지 번호 입니다."),
    INVALID_PAGE_SIZE(HttpStatus.BAD_REQUEST, "잘못된 페이지 사이즈 입니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "인증되지 않은 요청입니다."), //토큰 문제
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "인가되지 않은 사용자입니다."), //토큰은 존재, 역할 문제
    REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "요청 시간이 초과되었습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    ROLE_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "유효 하지 않은 롤 입니다."),
    AUDITOR_NON_MVC_REQUEST(HttpStatus.UNAUTHORIZED, "디스패처서블릿 요청이 아닙니다."),
    AUDITOR_HEADER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Auditor 헤더를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
