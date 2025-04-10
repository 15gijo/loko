package com.team15gijo.user.presentation.controller.v1;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.user.application.service.UserApplicationService;
import com.team15gijo.user.presentation.dto.v1.UserSignUpRequestDto;
import com.team15gijo.user.presentation.dto.v1.UserSignUpResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicatoinService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignUpResponseDto>> createUser(
            @RequestBody @Valid UserSignUpRequestDto userSignUpRequestDto
    ) {
        UserSignUpResponseDto userSignUpResponseDto = userApplicatoinService.createUser(userSignUpRequestDto);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", userSignUpResponseDto));
    }
}
