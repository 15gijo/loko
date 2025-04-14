package com.team15gijo.user.domain.exception;

import com.team15gijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserDomainExceptionCode implements ExceptionCode {

    USER_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "유효 하지 않은 유저 타입 입니다."),
    DUPLICATED_USER_EMAIL(HttpStatus.BAD_REQUEST, "중복된 이메일 입니다."),
    DUPLICATED_USER_NICKNAME(HttpStatus.BAD_REQUEST, "중복된 닉네임 입니다."),
    USER_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "유저 이메일을 찾을 수 없습니다."),
    USER_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "유저 아이디를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String message;


}
