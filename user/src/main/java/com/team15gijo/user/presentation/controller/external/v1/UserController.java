package com.team15gijo.user.presentation.controller.external.v1;

import com.team15gijo.common.annotation.RoleGuard;
import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.common.utils.page.PageableUtils;
import com.team15gijo.user.application.service.UserApplicationService;
import com.team15gijo.user.domain.model.UserStatus;
import com.team15gijo.user.presentation.dto.v1.AdminUserReadResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserReadResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserReadsResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserSignUpRequestDto;
import com.team15gijo.user.presentation.dto.v1.UserSignUpResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        UserSignUpResponseDto userSignUpResponseDto = userApplicatoinService.createUser(
                userSignUpRequestDto);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", userSignUpResponseDto));
    }

    //유저 단건 조회 - 관리자
    @RoleGuard(value = "ADMIN")
    @GetMapping("/admin/{userId}")
    public ResponseEntity<ApiResponse<AdminUserReadResponseDto>> getUserForAdmin(
            @PathVariable("userId") Long userId) {
        AdminUserReadResponseDto adminUserReadResponseDto = userApplicatoinService.getUserForAdmin(
                userId);
        return ResponseEntity.ok(ApiResponse.success("유저 단건 조회 성공", adminUserReadResponseDto));
    }

    //유저 전체 조회 - 관리자
    @RoleGuard(value = "ADMIN")
    @GetMapping("/admin/search")
    public ResponseEntity<ApiResponse<Page<AdminUserReadResponseDto>>> searchUsersForAdmin(
            @RequestParam(
                    name = "userId",
                    required = false
            ) Long userId,
            @RequestParam(
                    name = "username",
                    defaultValue = "",
                    required = false
            ) String username,
            @RequestParam(
                    name = "nickname",
                    defaultValue = "",
                    required = false
            ) String nickname,
            @RequestParam(
                    name = "email",
                    defaultValue = "",
                    required = false
            ) String email,
            @RequestParam(
                    name = "userStatus",
                    required = false
            ) UserStatus userStatus,
            @RequestParam(
                    name = "region",
                    defaultValue = "",
                    required = false
            ) String region,
            @PageableDefault(
                    size = 10,
                    page = 1,
                    sort = {"createdAt", "updatedAt"},
                    direction = Direction.ASC
            ) Pageable pageable
    ) {
        Pageable validatedPageable = PageableUtils.validate(pageable);
        Page<AdminUserReadResponseDto> adminUserReadResponseDtoPage = userApplicatoinService.searchUsersForAdmin(
                userId,
                username,
                nickname,
                email,
                userStatus,
                region,
                validatedPageable
        );
        return ResponseEntity.ok(ApiResponse.success("유저 전체 검색 성공", adminUserReadResponseDtoPage));
    }

    //내 정보 조회 - 유저
    @RoleGuard(min = "USER")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserReadResponseDto>> getUser(
            @RequestHeader("X-User-Id") Long userId) {
        UserReadResponseDto userReadResponseDto = userApplicatoinService.getUser(userId);
        return ResponseEntity.ok(ApiResponse.success("내 정보 조회 성공", userReadResponseDto));
    }

    //다른 유저 검색 - 유저
    @RoleGuard(min = "USER")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UserReadsResponseDto>>> searchUserReads(
            @RequestParam(
                    name = "nickname",
                    defaultValue = "",
                    required = false
            ) String nickname,
            @RequestParam(
                    name = "username",
                    defaultValue = "",
                    required = false
            ) String username,
            @RequestParam(
                    name = "region",
                    defaultValue = "",
                    required = false
            ) String region,
            @PageableDefault(
                    size = 10,
                    page = 1,
                    sort = {"createdAt", "updatedAt"},
                    direction = Direction.ASC
            ) Pageable pageable
    ) {
        Pageable validatedPageable = PageableUtils.validate(pageable);
        Page<UserReadsResponseDto> userReadsResponseDtoPage = userApplicatoinService.searchUsers(
                nickname,
                username,
                region,
                validatedPageable);
        return ResponseEntity.ok(ApiResponse.success("다른 유저 검색 성공", userReadsResponseDtoPage));
    }

    //상세조회 - 유저
    @RoleGuard(min = "USER")
    @GetMapping("/{nickname}")
    public ResponseEntity<ApiResponse<UserReadResponseDto>> getUserForUser(
            @PathVariable String nickname
    ) {
        UserReadResponseDto userReadResponseDto = userApplicatoinService.getUserForUser(nickname);
        return ResponseEntity.ok(ApiResponse.success("유저 상세 조회 성공", userReadResponseDto));
    }
}
