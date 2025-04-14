package com.team15gijo.auth.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team15gijo.auth.domain.repository.AuthRepository;
import com.team15gijo.auth.infrastructure.client.UserServiceClient;
import com.team15gijo.auth.infrastructure.jwt.JwtProvider;
import com.team15gijo.auth.infrastructure.security.CustomAuthenticationFilter;
import com.team15gijo.auth.infrastructure.security.CustomAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final UserServiceClient userServiceClient;
    private final AuthRepository authRepository;

    /**
     * Spring Security의 HTTP 보안 필터 체인을 구성 내부 서비스 간 통신 경로는 인증 없이 접근을 허용, 나머지 경로는 인증이 필요
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            AuthenticationManager authenticationManager) throws Exception {

        CustomAuthenticationFilter filter = new CustomAuthenticationFilter(
                authenticationManager,
                objectMapper,
                jwtProvider,
                userServiceClient);
        filter.setFilterProcessesUrl("/api/v1/auth/login");

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/internal/**", "/api/v1/auth/login", "/api/v1/auth/admin-assign").permitAll()
                        .requestMatchers("/api/v1/auth/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(customAuthenticationProvider())
                .addFilterAt(filter, UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(
                authRepository,
                passwordEncoder()
        );
    }

}
