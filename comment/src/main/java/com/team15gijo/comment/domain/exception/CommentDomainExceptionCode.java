package com.team15gijo.comment.domain.exception;

import com.team15gijo.common.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

public enum CommentDomainExceptionCode implements ExceptionCode {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    INVALID_COMMENT_CONTENT(HttpStatus.BAD_REQUEST, "잘못된 댓글 내용입니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    CommentDomainExceptionCode(HttpStatus httpStatus, String message) {
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
