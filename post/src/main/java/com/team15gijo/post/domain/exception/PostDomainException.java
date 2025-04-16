package com.team15gijo.post.domain.exception;

import com.team15gijo.common.exception.CustomException;

public class PostDomainException extends CustomException {

    public PostDomainException(PostDomainExceptionCode errorCode) {
        super(errorCode);
    }

    public PostDomainException(PostDomainExceptionCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
