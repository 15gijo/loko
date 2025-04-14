package com.team15gijo.auth.presentation.controller.external.v1;

import com.team15gijo.auth.application.service.AuthApplicationService;
import com.team15gijo.auth.presentation.dto.v1.AssignAdminRequestDto;
import com.team15gijo.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    @PutMapping("/admin-assign")
    public ResponseEntity<ApiResponse<String>> assignAdmin(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody AssignAdminRequestDto assignAdminRequestDto
    ) {
        authApplicationService.assignAdmin(token, assignAdminRequestDto);
        return ResponseEntity.ok(ApiResponse.success("관리자 권한 부여 성공"));
    }
}
