package com.team15gijo.user.application.service;

import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.domain.model.UserStatus;
import com.team15gijo.user.infrastructure.dto.UserFeignInfoResponseDto;
import com.team15gijo.user.presentation.dto.internal.response.v1.UserAndRegionInfoFollowResponseDto;
import com.team15gijo.user.presentation.dto.internal.response.v1.UserInfoFollowResponseDto;
import com.team15gijo.user.presentation.dto.request.v1.AdminUserStatusUpdateRequestDto;
import com.team15gijo.user.presentation.dto.request.v1.UserEmailUpdateRequestDto;
import com.team15gijo.user.presentation.dto.request.v1.UserOAuthSignUpRequestDto;
import com.team15gijo.user.presentation.dto.request.v1.UserPasswordUpdateRequestDto;
import com.team15gijo.user.presentation.dto.request.v1.UserSignUpRequestDto;
import com.team15gijo.user.presentation.dto.request.v1.UserUpdateRequestDto;
import com.team15gijo.user.presentation.dto.response.v1.UserReadResponseDto;
import com.team15gijo.user.presentation.dto.response.v1.UserSignUpResponseDto;
import com.team15gijo.user.presentation.dto.response.v1.UserUpdateResponseDto;
import com.team15gijo.user.presentation.dto.v1.AdminUserReadResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserReadsResponseDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserApplicationService {

    UserSignUpResponseDto createUser(UserSignUpRequestDto userSignUpRequestDto);

    UserFeignInfoResponseDto getUserInfo(String identifier);

    AdminUserReadResponseDto getUserForAdmin(Long userId);

    Long getUserIdByNickname(String nickname);

    Page<AdminUserReadResponseDto> searchUsersForAdmin(Long userId, String username,
            String nickname, String email, UserStatus userStatus, String region,
            Pageable validatedPageable);

    UserReadResponseDto getUser(Long userId);

    String getEmailByUserId(Long userId);

    Page<UserReadsResponseDto> searchUsers(String nickname, String username, String region,
            Pageable validatedPageable);

    UserReadResponseDto getUserForUser(String nickname);

    UserUpdateResponseDto updateUser(Long userId, UserUpdateRequestDto userUpdateRequestDto);

    void updateEmailUser(Long userId, @Valid UserEmailUpdateRequestDto userEmailUpdateRequestDto);

    void updatePasswordUser(Long userId,
            @Valid UserPasswordUpdateRequestDto userPasswordUpdateRequestDto);

    void updateUserStatus(@Valid AdminUserStatusUpdateRequestDto adminUserStatusUpdateRequestDto);

    void deleteUser(Long userId);

    UserInfoFollowResponseDto getUserInfoForFollow(Long userId);

    List<UserAndRegionInfoFollowResponseDto> getUserAndRegionInfoForRecommend(
            Long myUserId,
            List<Long> candidateUserIds);

    void increaseFollowCount(Long followerId, Long followeeId);

    void decreaseFollowCount(Long followerId, Long followeeId);

    List<UserEntity> getAllUsers();

    UserSignUpResponseDto createUserOauth(@Valid UserOAuthSignUpRequestDto userOAuthSignUpRequestDto);
}
