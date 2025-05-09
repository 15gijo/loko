package com.team15gijo.user.presentation.controller.v2;

import com.team15gijo.common.annotation.RoleGuard;
import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.common.utils.page.PageableUtils;
import com.team15gijo.user.application.service.UserApplicationService;
import com.team15gijo.user.domain.model.UserStatus;
import com.team15gijo.user.presentation.dto.request.v1.AdminUserStatusUpdateRequestDto;
import com.team15gijo.user.presentation.dto.request.v1.UserEmailUpdateRequestDto;
import com.team15gijo.user.presentation.dto.request.v1.UserPasswordUpdateRequestDto;
import com.team15gijo.user.presentation.dto.request.v1.UserSignUpRequestDto;
import com.team15gijo.user.presentation.dto.request.v1.UserUpdateRequestDto;
import com.team15gijo.user.presentation.dto.request.v1.UserOAuthSignUpRequestDto;
import com.team15gijo.user.presentation.dto.response.v1.UserReadResponseDto;
import com.team15gijo.user.presentation.dto.response.v1.UserSignUpResponseDto;
import com.team15gijo.user.presentation.dto.response.v1.UserUpdateResponseDto;
import com.team15gijo.user.presentation.dto.v1.AdminUserReadResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserReadsResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v2/users")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignUpResponseDto>> createUser(
            @RequestBody @Valid UserSignUpRequestDto userSignUpRequestDto
    ) {
        UserSignUpResponseDto userSignUpResponseDto = userApplicationService.createUser(
                userSignUpRequestDto);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", userSignUpResponseDto));
    }

    //OAuth 회원가입
    @PostMapping("/signup/oauth")
    public ResponseEntity<ApiResponse<UserSignUpResponseDto>> createUserOauth(
            @RequestBody @Valid UserOAuthSignUpRequestDto userOAuthSignUpRequestDto
    ) {
        UserSignUpResponseDto userSignUpResponseDto = userApplicationService.createUserOauth(
                userOAuthSignUpRequestDto
        );
        return ResponseEntity.ok(ApiResponse.success("OAuth 회원가입 성공", userSignUpResponseDto));
    }

    //유저 단건 조회 - 관리자
    @RoleGuard(value = "ADMIN")
    @GetMapping("/admin/{userId}")
    public ResponseEntity<ApiResponse<AdminUserReadResponseDto>> getUserForAdmin(
            @PathVariable("userId") Long userId) {
        AdminUserReadResponseDto adminUserReadResponseDto = userApplicationService.getUserForAdmin(
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
        Page<AdminUserReadResponseDto> adminUserReadResponseDtoPage = userApplicationService.searchUsersForAdmin(
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
        UserReadResponseDto userReadResponseDto = userApplicationService.getUser(userId);
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
        Page<UserReadsResponseDto> userReadsResponseDtoPage = userApplicationService.searchUsers(
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
        UserReadResponseDto userReadResponseDto = userApplicationService.getUserForUser(nickname);
        return ResponseEntity.ok(ApiResponse.success("유저 상세 조회 성공", userReadResponseDto));
    }

    //내 정보 수정 - ALL
    @RoleGuard(min = "USER")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserUpdateResponseDto>> updateUser(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody UserUpdateRequestDto userUpdateRequestDto
    ) {
        UserUpdateResponseDto userUpdateResponseDto = userApplicationService.updateUser(
                userId, userUpdateRequestDto);
        return ResponseEntity.ok(ApiResponse.success("내 정보 수정 성공", userUpdateResponseDto));
    }

    //이메일 수정 - ALL
    @RoleGuard(min = "USER")
    @PatchMapping("/email")
    public ResponseEntity<ApiResponse<Void>> updateEmailUser(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserEmailUpdateRequestDto userEmailUpdateRequestDto
    ) {
        userApplicationService.updateEmailUser(userId, userEmailUpdateRequestDto);
        return ResponseEntity.ok(ApiResponse.success("이메일 업데이트 성공"));
    }

    //비밀번호 수정 - ALL
    @RoleGuard(min = "USER")
    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePasswordUser(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserPasswordUpdateRequestDto userPasswordUpdateRequestDto
    ) {
        userApplicationService.updatePasswordUser(userId, userPasswordUpdateRequestDto);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 업데이트 성공"));
    }

    //유저 상태 수정 - 관리자
    @RoleGuard(value = "ADMIN")
    @PatchMapping("/admin/user-status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @Valid @RequestBody AdminUserStatusUpdateRequestDto adminUserStatusUpdateRequestDto
    ) {
        userApplicationService.updateUserStatus(adminUserStatusUpdateRequestDto);
        return ResponseEntity.ok(
                ApiResponse.success("유저 상태 변경 성공"));
    }

    //유저 상태 수정 - 매니저 -> v2 때 비즈니스 로직 고려

    //회원탈퇴 -> 추후 postmapping 및 auth 로직 리팩토링 필요
    @RoleGuard(min = "USER")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @RequestHeader("X-User-Id") Long userId
    ) {
        userApplicationService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴 성공"));
    }
}
