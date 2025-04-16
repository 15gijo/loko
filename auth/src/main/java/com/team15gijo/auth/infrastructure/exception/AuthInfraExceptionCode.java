package com.team15gijo.auth.infrastructure.exception;

import com.team15gijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthInfraExceptionCode implements ExceptionCode {

    LOGIN_REQUEST_PARSING_FAILED(HttpStatus.BAD_REQUEST, "로그인 요청 파싱 실패"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "엑세스 토큰이 유효하지 않습니다."),
    JSON_WRITE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "응답 JSON 생성에 실패했습니다."),
    UNAUTHORIZED_LOGIN(HttpStatus.UNAUTHORIZED, "로그인 인증에 실패하였습니다."),
    USER_ALREADY_WITHDRAWN(HttpStatus.FORBIDDEN, "이미 탈퇴한 회원입니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
