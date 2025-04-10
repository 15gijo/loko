package com.team15gijo.comment.domain.exception;

import com.team15gijo.common.exception.CustomException;

public class CommentDomainException extends CustomException {

    public CommentDomainException(CommentDomainExceptionCode errorCode) {
        super(errorCode);
    }

    public CommentDomainException(CommentDomainExceptionCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
