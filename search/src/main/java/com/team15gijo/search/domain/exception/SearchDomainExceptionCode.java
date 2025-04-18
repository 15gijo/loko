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
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
