package com.team15gijo.user.infrastructure.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class AuthFeignClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorDecoder();
    }
}
