package com.team15gijo.user.application.service;

import com.team15gijo.user.domain.model.UserStatus;
import com.team15gijo.user.infrastructure.dto.UserFeignInfoResponseDto;
import com.team15gijo.user.presentation.dto.v1.AdminUserReadResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserReadResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserReadsResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserSignUpRequestDto;
import com.team15gijo.user.presentation.dto.v1.UserSignUpResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserApplicationService {

    UserSignUpResponseDto createUser(@Valid UserSignUpRequestDto userSignUpRequestDto);

    UserFeignInfoResponseDto getUserInfo(String identifier);

    AdminUserReadResponseDto getUserForAdmin(Long userId);

    Long getUserIdByNickname(String nickname);

    Page<AdminUserReadResponseDto> searchUsersForAdmin(Long userId, String username,
            String nickname, String email, UserStatus userStatus, String region, Pageable validatedPageable);

    UserReadResponseDto getUser(Long userId);

    String getEmailByUserId(Long userId);

    Page<UserReadsResponseDto> searchUsers(String nickname, String username, String region,
            Pageable validatedPageable);

    UserReadResponseDto getUserForUser(String nickname);
}
