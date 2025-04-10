package com.team15gijo.auth.presentation.controller.v1;

import com.team15gijo.auth.application.service.AuthApplicationService;
import com.team15gijo.auth.presentation.dto.v1.AuthLoginRequestDto;
import com.team15gijo.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(
            @Valid @RequestBody AuthLoginRequestDto authLoginRequestDto
    ) {
        authApplicationService.login(authLoginRequestDto);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공"));
    }
}
