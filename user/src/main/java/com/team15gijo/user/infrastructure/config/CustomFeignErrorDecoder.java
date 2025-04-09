package com.team15gijo.user.infrastructure.config;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.user.application.exception.UserApplicationExceptionCode;
import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.ws.rs.InternalServerErrorException;

public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        if (response.status() == 500) {
            String errorMessage = response.body().toString();
            return new InternalServerErrorException("서버 내부 오류: " + errorMessage);
        }

        if (methodKey.contains("signup") && response.status() == 400) {
            return new CustomException(UserApplicationExceptionCode.AUTH_SERVICE_SIGNUP_FAILED);
        }
        return errorDecoder.decode(methodKey, response);
    }
}
