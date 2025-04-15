package com.team15gijo.user.application.service.v1;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.user.application.dto.v1.AdminUserSearchCommand;
import com.team15gijo.user.application.service.UserApplicationService;
import com.team15gijo.user.domain.exception.UserDomainExceptionCode;
import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.domain.model.UserStatus;
import com.team15gijo.user.domain.repository.UserRepository;
import com.team15gijo.user.domain.service.UserDomainService;
import com.team15gijo.user.infrastructure.client.AuthServiceClient;
import com.team15gijo.user.infrastructure.dto.UserFeignInfoResponseDto;
import com.team15gijo.user.infrastructure.dto.v1.internal.AuthSignUpRequestDto;
import com.team15gijo.user.infrastructure.dto.v1.internal.AuthSignUpUpdateUserIdRequestDto;
import com.team15gijo.user.presentation.dto.v1.AdminUserReadResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserReadResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserReadsResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserSignUpRequestDto;
import com.team15gijo.user.presentation.dto.v1.UserSignUpResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserDomainService userDomainService;
    private final UserRepository userRepository;
    private final AuthServiceClient authServiceClient;

    @Override
    @Transactional
    public UserSignUpResponseDto createUser(UserSignUpRequestDto userSignUpRequestDto) {

        //유저 생성
        UserEntity createdUser = userDomainService.createUser(userSignUpRequestDto);

        //인증 서버로 회원가입 알림
        AuthSignUpRequestDto authSignUpRequsetDto =
                AuthSignUpRequestDto.builder()
                        .nickname(createdUser.getNickName())
                        .identifier(createdUser.getEmail())
                        .password(userSignUpRequestDto.password())
                        .loginTypeName("PASSWORD")
                        .build();

        UUID authId = authServiceClient.signUp(authSignUpRequsetDto);

        //유저 DB save
        UserEntity savedUser = userRepository.save(createdUser);

        //유저 createdBy 업데이트
        userRepository.updateCreatedBy(savedUser.getId());

        //인증 서버로 userId 알림
        AuthSignUpUpdateUserIdRequestDto authSignUpUpdateUserIdRequestDto = new AuthSignUpUpdateUserIdRequestDto(
                authId, savedUser.getId());
        authServiceClient.updateId(authSignUpUpdateUserIdRequestDto);

        return new UserSignUpResponseDto(
                savedUser.getEmail(),
                savedUser.getNickName(),
                savedUser.getUserName(),
                savedUser.getRegion(),
                savedUser.getProfile());
    }

    @Override
    public AdminUserReadResponseDto getUserForAdmin(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserDomainExceptionCode.USER_NOT_FOUND));
        return AdminUserReadResponseDto.from(user);
    }

    @Override
    public Page<AdminUserReadResponseDto> searchUsersForAdmin(Long userId, String username,
            String nickname, String email, UserStatus userStatus, String region,
            Pageable validatedPageable) {
        AdminUserSearchCommand adminUserSearchCommand = AdminUserSearchCommand.builder()
                .userId(userId)
                .username(username)
                .nickname(nickname)
                .email(email)
                .userStatus(userStatus)
                .region(region)
                .build();
        return userRepository.searchUsersForAdmin(adminUserSearchCommand, validatedPageable);
    }

    @Override
    public UserReadResponseDto getUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserDomainExceptionCode.USER_NOT_FOUND));
        return UserReadResponseDto.from(user);
    }

    @Override
    public String getEmailByUserId(Long userId) {
        String email = userRepository.findEmailById(userId)
                .orElseThrow(
                        () -> new CustomException(UserDomainExceptionCode.USER_EMAIL_NOT_FOUND));
        return email;
    }

    @Override
    public Page<UserReadsResponseDto> searchUsers(String nickname, String username, String region,
            Pageable validatedPageable) {
        return userRepository.searchUsers(nickname, username, region, validatedPageable);
    }

    @Override
    public UserReadResponseDto getUserForUser(String nickname) {
        UserEntity user = userRepository.findByNickName(nickname)
                .orElseThrow(() -> new CustomException(UserDomainExceptionCode.USER_NOT_FOUND));
        return UserReadResponseDto.from(user);
    }

    //internal
    @Override
    public UserFeignInfoResponseDto getUserInfo(String identifier) {

        UserEntity user = userRepository.findByEmail(identifier)
                .orElseThrow(
                        () -> new CustomException(UserDomainExceptionCode.USER_NOT_FOUND));
        return new UserFeignInfoResponseDto(
                user.getId(),
                user.getNickName(),
                user.getRegion()
        );
    }

    //internal
    @Override
    public Long getUserIdByNickname(String nickname) {
        Long userId = userRepository.findIdByNickName(nickname)
                .orElseThrow(() -> new CustomException(UserDomainExceptionCode.USER_ID_NOT_FOUND));
        return userId;
    }

}
