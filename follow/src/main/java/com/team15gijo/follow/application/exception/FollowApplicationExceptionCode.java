package com.team15gijo.follow.application.exception;

import com.team15gijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FollowApplicationExceptionCode implements ExceptionCode {
    USER_SERVICE_GET_FOLLOWING_FAILED(HttpStatus.BAD_REQUEST, "유저 서비스 팔로잉 유저 정보 가져오기 연동 실패"),
    USER_SERVICE_GET_FOLLOWER_FAILED(HttpStatus.BAD_REQUEST, "유저 서비스 팔로워 유저 정보 가져오기 연동 실패");


    private final HttpStatus httpStatus;
    private final String message;
}
