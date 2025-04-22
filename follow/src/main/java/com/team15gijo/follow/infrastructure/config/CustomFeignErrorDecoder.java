package com.team15gijo.follow.infrastructure.config;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.follow.application.exception.FollowApplicationExceptionCode;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        if (methodKey.contains("following") && response.status() == 400) {
            return new CustomException(FollowApplicationExceptionCode.USER_SERVICE_GET_FOLLOWING_FAILED);
        }
        if (methodKey.contains("follower") && response.status() == 400) {
            return new CustomException(FollowApplicationExceptionCode.USER_SERVICE_GET_FOLLOWER_FAILED);
        }
        return errorDecoder.decode(methodKey, response);
    }
}
