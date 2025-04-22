package com.team15gijo.follow.infrastructure.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class UserFeignClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorDecoder();
    }
}
