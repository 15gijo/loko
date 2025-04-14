package com.team15gijo.common.config;

import com.team15gijo.common.interceptor.RoleGuardInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RoleGuardInterceptor roleGuardInterceptor;

    //롤가드 필요한 api
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleGuardInterceptor)
            .addPathPatterns("/api/v1/chats/**")
                .addPathPatterns("/api/v1/users/**");
    }
}
