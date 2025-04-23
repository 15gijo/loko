package com.team15gijo.auth.presentation.controller.v2;

import com.team15gijo.auth.application.service.AuthApplicationService;
import com.team15gijo.auth.presentation.dto.request.v1.AdminAssignManagerRequestDto;
import com.team15gijo.auth.presentation.dto.request.v1.AssignAdminRequestDto;
import com.team15gijo.auth.presentation.dto.response.v2.AuthRefreshResponseDto;
import com.team15gijo.common.annotation.RoleGuard;
import com.team15gijo.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auth")
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

    //매니저 롤 부여 - 관리자
    @RoleGuard(value = "ADMIN")
    @PatchMapping("/admin/manager-assign")
    public ResponseEntity<ApiResponse<Void>> assignManager(
            @Valid @RequestBody AdminAssignManagerRequestDto adminAssignManagerRequestDto
    ) {
        authApplicationService.assignManger(adminAssignManagerRequestDto);
        return ResponseEntity.ok(ApiResponse.success("매니저 권한 부여 성공"));
    }

    //accessToken 재발급
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthRefreshResponseDto>> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        AuthRefreshResponseDto authRefreshResponseDto = authApplicationService.refresh(request,
                response);
        return ResponseEntity.ok(ApiResponse.success("AccessToken 재발급 성공", authRefreshResponseDto));
    }

    //로그아웃
    @RoleGuard(min = "USER")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") Long userId
    ) {
        String accessToken = token.substring(7);
        authApplicationService.logout(accessToken, userId);
        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공"));
    }
}
