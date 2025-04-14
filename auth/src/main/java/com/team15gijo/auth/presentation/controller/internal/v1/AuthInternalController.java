package com.team15gijo.auth.presentation.controller.internal.v1;

import com.team15gijo.auth.application.service.AuthApplicationService;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpRequestDto;
import com.team15gijo.auth.infrastructure.dto.v1.internal.AuthSignUpUpdateUserIdRequestDto;
import com.team15gijo.common.dto.ApiResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<UUID> signUp(
            @RequestBody AuthSignUpRequestDto authSignUpRequestDto) {
        log.info("signUp authSignUpRequestDto={}", authSignUpRequestDto);
        UUID authId = authApplicationService.signUp(authSignUpRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(authId);
    }

    @PutMapping("/signup-updateUserId")
    public ResponseEntity<ApiResponse<Void>> updateId(
            @RequestBody AuthSignUpUpdateUserIdRequestDto authSignUpUpdateUserIdRequestDto
    ) {
        log.info("updateId authSignUpUserIdRequestDto={}", authSignUpUpdateUserIdRequestDto);
        authApplicationService.signUpUpdateUserId(authSignUpUpdateUserIdRequestDto);
        return ResponseEntity.ok(ApiResponse.success("인증 서버 회원 가입 유저 아이디 업데이트, createdBy 업데이트 성공"));
    }
}
