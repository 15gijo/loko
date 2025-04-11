package com.team15gijo.auth.infrastructure.security;

import com.team15gijo.auth.domain.exception.AuthDomainExceptionCode;
import com.team15gijo.auth.domain.model.AuthEntity;
import com.team15gijo.auth.domain.repository.AuthRepository;
import com.team15gijo.common.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        //필터에서 만든 authToken 전달됨
        String identifier = authentication.getName();
        String password = authentication.getCredentials().toString();

        //인증 조회
        AuthEntity auth = authRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new CustomException(
                        AuthDomainExceptionCode.USER_IDENTIFIER_NOT_FOUND));

        //비밀번호 검증
        if (!passwordEncoder.matches(
                password,
                auth.getPassword())) {
            throw new CustomException(AuthDomainExceptionCode.INVALID_PASSWORD);
        }

        return new UsernamePasswordAuthenticationToken(
                auth.getIdentifier(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + auth.getRole()))
        );
    }

    /**
     * 이 AuthenticationProvider가 지원하는 Authentication 타입을 명시합니다.
     * <p>
     * Spring Security는 이 메서드를 통해 현재 인증 요청(Authentication 객체)이 해당 Provider에서 처리 가능한 타입인지 확인합니다.
     * <p>
     * 여기서는 UsernamePasswordAuthenticationToken 타입을 지원하도록 설정합니다. 만약 이 메서드에서 true를 반환하지 않으면,
     * authenticate() 메서드는 호출되지 않습니다.
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
