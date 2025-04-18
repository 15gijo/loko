package com.team15gijo.auth.presentation.controller.v1;

import com.team15gijo.auth.application.service.AuthApplicationService;
import com.team15gijo.auth.presentation.dto.internal.request.v1.AuthIdentifierUpdateRequestDto;
import com.team15gijo.auth.presentation.dto.internal.request.v1.AuthPasswordUpdateRequestDto;
import com.team15gijo.auth.presentation.dto.internal.request.v1.AuthSignUpRequestDto;
import com.team15gijo.auth.presentation.dto.internal.request.v1.AuthSignUpUpdateUserIdRequestDto;
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
@RequestMapping("internal/api/v1/auth")
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

    @PostMapping("/identifier-update")
    public ResponseEntity<ApiResponse<Void>> updateIdentifier(
            @RequestBody AuthIdentifierUpdateRequestDto authIdentifierUpdateRequestDto) {
        log.info("updateIdentifier authIdentifierUpdateRequestDto={}",
                authIdentifierUpdateRequestDto);
        authApplicationService.updateIdentifier(authIdentifierUpdateRequestDto);
        return ResponseEntity.ok(ApiResponse.success("인증 서버 identifier 업데이트 성공"));
    }

    @PostMapping("/password-update")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @RequestBody AuthPasswordUpdateRequestDto authPasswordUpdateRequestDto) {
        log.info("updatePassword authPasswordUpdateRequestDto={}",
                authPasswordUpdateRequestDto); //추후 password는 로그에서 제외
        authApplicationService.updatePassword(authPasswordUpdateRequestDto);
        return ResponseEntity.ok(ApiResponse.success("인증 서버 password 업데이트 성공"));
    }
}
