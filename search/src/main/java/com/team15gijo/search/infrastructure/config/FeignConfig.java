package com.team15gijo.search.infrastructure.config;

import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }

    @Bean
    public RequestInterceptor charsetInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Content-Type", "application/json; charset=UTF-8");
            requestTemplate.header("Accept-Charset", "UTF-8");
        };
    }
}
