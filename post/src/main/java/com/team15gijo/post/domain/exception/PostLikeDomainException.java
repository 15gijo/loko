package com.team15gijo.post.domain.exception;

import com.team15gijo.common.exception.CustomException;

public class PostLikeDomainException extends CustomException {

    public PostLikeDomainException(PostDomainExceptionCode errorCode) {
        super(errorCode);
    }

    public PostLikeDomainException(PostDomainExceptionCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
