package com.team15gijo.post.domain.exception;

import com.team15gijo.common.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

public enum PostDomainExceptionCode implements ExceptionCode {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    INVALID_POST_CONTENT(HttpStatus.BAD_REQUEST, "잘못된 게시글 내용입니다.") ,
    NOT_OWNER(HttpStatus.FORBIDDEN, "본인의 게시글이 아닙니다."),
    ALREADY_LIKED(HttpStatus.BAD_REQUEST, "사용자가 이미 좋아요를 눌렀습니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    PostDomainExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
