package com.team15gijo.auth.infrastructure.config;

import com.team15gijo.auth.application.exception.AuthApplicationExceptionCode;
import com.team15gijo.common.exception.CustomException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        if (methodKey.contains("info") && response.status() == 400) {
            return new CustomException(AuthApplicationExceptionCode.USER_SERVICE_INFO_FAILED);
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
