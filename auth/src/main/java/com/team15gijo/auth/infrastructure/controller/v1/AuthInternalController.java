package com.team15gijo.auth.infrastructure.controller.v1;

import com.team15gijo.auth.application.service.AuthApplicationService;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpRequestDto;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/internal/auth")
@RequiredArgsConstructor
public class AuthInternalController {

    private final AuthApplicationService authApplicationService;

    @PostMapping("/signup")
    public AuthSignUpResponseDto signUp(
            @RequestBody AuthSignUpRequestDto authSignUpRequestDto) {
        log.info("signUp authSignUpRequestDto={}", authSignUpRequestDto);
        return authApplicationService.signUp(authSignUpRequestDto);
    }
}
