package com.team15gijo.search.domain.exception;

import com.team15gijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SearchDomainExceptionCode implements ExceptionCode {

    POST_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "게시글 서버와 통신에 실패했습니다."),
    USER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "유저 서버와 통신에 실패했습니다."),
    INVALID_SEARCH(HttpStatus.BAD_REQUEST, "유효하지 않은 검색입니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    POST_SAVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 저장에 실패했습니다"),
    USER_SAVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "유저 저장에 실패했습니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다"),
    POST_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 삭제에 실패했습니다"),
    POST_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 수정에 실패했습니다"),
    VIEW_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "조회수 수정에 실패했습니다"),
    COMMENT_COUNT_UP_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "댓글수 증가가 실패했습니다"),
    COMMENT_COUNT_DOWN_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "댓글수 감소가 실패했습니다"),
    LIKE_COUNT_UP_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "좋아요수 증가가 실패했습니다"),
    LIKE_COUNT_DOWN_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "좋아요수 감소가 실패했습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
