package com.team15gijo.follow.domain.exception;

import com.team15gijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FollowDomainExceptionCode implements ExceptionCode {

    FOLLOW_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로우 타입이 유효하지 않습니다."),
    CANNOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, "자기 자신을 팔로우 할 수 없습니다."),
    ALREADY_FOLLOWING(HttpStatus.BAD_REQUEST, "이미 팔로우 한 아이디 입니다."),
    FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 팔로우를 찾을 수 없습니다."),
    CANNOT_BLOCK_SELF(HttpStatus.BAD_REQUEST, "자기 자신을 블락 할 수 없습니다."),
    ALREADY_BLOCKED(HttpStatus.BAD_REQUEST, "이미 블락 한 아이디 입니다."),
    NOT_BLOCKED(HttpStatus.BAD_REQUEST, "블락한 관계가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
