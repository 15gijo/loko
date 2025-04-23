package com.team15gijo.user.application.service.v1;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.user.application.dto.v1.AdminUserSearchCommand;
import com.team15gijo.user.application.exception.UserApplicationExceptionCode;
import com.team15gijo.user.application.service.UserApplicationService;
import com.team15gijo.user.domain.exception.UserDomainExceptionCode;
import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.domain.model.UserStatus;
import com.team15gijo.user.domain.repository.UserRepository;
import com.team15gijo.user.domain.service.UserDomainService;
import com.team15gijo.user.infrastructure.client.AuthServiceClient;
import com.team15gijo.user.infrastructure.dto.UserFeignInfoResponseDto;
import com.team15gijo.user.infrastructure.dto.request.v1.AuthIdentifierUpdateRequestDto;
import com.team15gijo.user.infrastructure.dto.request.v1.AuthPasswordUpdateRequestDto;
import com.team15gijo.user.infrastructure.dto.request.v1.AuthSignUpRequestDto;
import com.team15gijo.user.infrastructure.dto.request.v1.AuthSignUpUpdateUserIdRequestDto;
import com.team15gijo.user.infrastructure.kakao.KakaoMapService;
import com.team15gijo.user.presentation.dto.internal.response.v1.UserInfoFollowResponseDto;
import com.team15gijo.user.presentation.dto.request.v1.AdminUserStatusUpdateRequestDto;
import com.team15gijo.user.presentation.dto.request.v1.UserEmailUpdateRequestDto;
import com.team15gijo.user.presentation.dto.request.v1.UserPasswordUpdateRequestDto;
import com.team15gijo.user.presentation.dto.request.v1.UserSignUpRequestDto;
import com.team15gijo.user.presentation.dto.request.v1.UserUpdateRequestDto;
import com.team15gijo.user.presentation.dto.response.v1.UserReadResponseDto;
import com.team15gijo.user.presentation.dto.response.v1.UserSignUpResponseDto;
import com.team15gijo.user.presentation.dto.response.v1.UserUpdateResponseDto;
import com.team15gijo.user.infrastructure.kafka.dto.UserElasticsearchRequestDto;
import com.team15gijo.user.infrastructure.kafka.service.KafkaProducerService;
import com.team15gijo.user.presentation.dto.v1.AdminUserReadResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserReadsResponseDto;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
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
    private final KafkaProducerService producerService;
    private final KakaoMapService kakaoMapService;

    @Override
    @Transactional
    public UserSignUpResponseDto createUser(UserSignUpRequestDto userSignUpRequestDto) {
        //카카오맵 주소 받기
        String kakaoMapRegion = kakaoMapService.getAddress(userSignUpRequestDto.region());

        //유저 생성
        UserEntity createdUser = userDomainService.createUser(
                userSignUpRequestDto.withRegion(kakaoMapRegion)
        );

        //인증 서버로 회원가입 알림
        AuthSignUpRequestDto authSignUpRequestDto =
                AuthSignUpRequestDto.builder()
                        .nickname(createdUser.getNickname())
                        .identifier(createdUser.getEmail())
                        .password(userSignUpRequestDto.password())
                        .loginTypeName("PASSWORD")
                        .build();

        UUID authId = authServiceClient.signUp(authSignUpRequestDto);

        //유저 DB save
        UserEntity savedUser = userRepository.save(createdUser);

        //유저 createdBy 업데이트
        userRepository.updateCreatedBy(savedUser.getId());

        //인증 서버로 userId 알림
        AuthSignUpUpdateUserIdRequestDto authSignUpUpdateUserIdRequestDto = new AuthSignUpUpdateUserIdRequestDto(
                authId, savedUser.getId());
        authServiceClient.updateId(authSignUpUpdateUserIdRequestDto);

        // kafka로 검색 서버에 유저 정보 전송
        log.info("user 정보 검색서버로 kafka 전송 시작");
        producerService.sendUserCreate(UserElasticsearchRequestDto.from(savedUser));
        log.info("user 정보 검색서버로 kafka 전송 완료");

        return new UserSignUpResponseDto(
                savedUser.getEmail(),
                savedUser.getNickname(),
                savedUser.getUsername(),
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
        UserEntity user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(UserDomainExceptionCode.USER_NOT_FOUND));
        return UserReadResponseDto.from(user);
    }

    @Override
    @Transactional
    public UserUpdateResponseDto updateUser(
            Long userId,
            UserUpdateRequestDto userUpdateRequestDto) {

        //유저존재 확인
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserDomainExceptionCode.USER_NOT_FOUND));

        //null -> 기존 유지, isBlank -> 예외
        safeUpdate(
                userUpdateRequestDto.username(),
                user.getUsername(),
                user::updateUsername,
                () -> new CustomException(UserApplicationExceptionCode.INVALID_UPDATE_PARAMETER)
        );
        safeUpdate(
                userUpdateRequestDto.nickname(),
                user.getNickname(),
                user::updateNickname,
                () -> new CustomException(UserApplicationExceptionCode.INVALID_UPDATE_PARAMETER)
        );
        safeUpdate(
                userUpdateRequestDto.profile(),
                user.getProfile(),
                user::updateProfile,
                () -> new CustomException(UserApplicationExceptionCode.INVALID_UPDATE_PARAMETER)
        );
        safeUpdate(
                userUpdateRequestDto.region(),
                user.getRegion(),
                user::updateRegion,
                () -> new CustomException(UserApplicationExceptionCode.INVALID_UPDATE_PARAMETER)
        );

        return UserUpdateResponseDto.from(user);
    }

    @Override
    @Transactional
    public void updateEmailUser(Long userId, UserEmailUpdateRequestDto userEmailUpdateRequestDto) {

        //유저 확인
        UserEntity user = userRepository.findByEmail(userEmailUpdateRequestDto.currentEmail())
                .orElseThrow(() -> new CustomException(UserDomainExceptionCode.USER_NOT_FOUND));

        //요청자 정보 유효 검사
        if (!user.getId().equals(userId)) {
            throw new CustomException(UserApplicationExceptionCode.UNAUTHORIZED_USER);
        }

        //인증 확인 및 업데이트
        authServiceClient.updateIdentifier(
                new AuthIdentifierUpdateRequestDto(
                        user.getId(),
                        userEmailUpdateRequestDto.password(),
                        userEmailUpdateRequestDto.newEmail()
                )
        );

        //이메일 업데이트
        user.updateEmail(userEmailUpdateRequestDto.newEmail());
    }

    @Override
    @Transactional
    public void updatePasswordUser(Long userId,
            UserPasswordUpdateRequestDto userPasswordUpdateRequestDto) {

        //유저 확인
        UserEntity user = userRepository.findByEmail(userPasswordUpdateRequestDto.email())
                .orElseThrow(() -> new CustomException(UserDomainExceptionCode.USER_NOT_FOUND));

        //요청자 정보 유효 검사
        if (!user.getId().equals(userId)) {
            throw new CustomException(UserApplicationExceptionCode.UNAUTHORIZED_USER);
        }

        //인증 확인 및 업데이트
        authServiceClient.updatePassword(
                new AuthPasswordUpdateRequestDto(
                        user.getId(),
                        userPasswordUpdateRequestDto.password(),
                        userPasswordUpdateRequestDto.newPassword()
                )
        );

    }

    @Override
    @Transactional
    public void updateUserStatus(AdminUserStatusUpdateRequestDto adminUserStatusUpdateRequestDto) {
        //유저 확인
        UserEntity user = userRepository.findById(adminUserStatusUpdateRequestDto.userId())
                .orElseThrow(() -> new CustomException(UserDomainExceptionCode.USER_NOT_FOUND));

        //enum으로 변환 및 검사
        UserStatus newUserStatus = UserStatus.fromUserStatusName(
                adminUserStatusUpdateRequestDto.targetUserStatusName());

        //업데이트
        user.updateUserStatus(newUserStatus);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        //유저 확인
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserDomainExceptionCode.USER_NOT_FOUND));

        //유저 탈퇴 상태 변경
        user.updateUserStatus(UserStatus.WITHDRAWN);

        //삭제
        userRepository.deleteById(userId);
    }

    //internal
    @Override
    public UserInfoFollowResponseDto getUserInfoForFollow(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserDomainExceptionCode.USER_NOT_FOUND));

        return new UserInfoFollowResponseDto(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getProfile()
        );
    }

    //internal
    @Override
    public UserFeignInfoResponseDto getUserInfo(String identifier) {

        UserEntity user = userRepository.findByEmail(identifier)
                .orElseThrow(
                        () -> new CustomException(UserDomainExceptionCode.USER_NOT_FOUND));
        return new UserFeignInfoResponseDto(
                user.getId(),
                user.getNickname(),
                user.getRegion(),
                user.getStatus().name()
        );
    }

    //internal
    @Override
    public Long getUserIdByNickname(String nickname) {
        Long userId = userRepository.findIdByNickname(nickname)
                .orElseThrow(() -> new CustomException(UserDomainExceptionCode.USER_ID_NOT_FOUND));
        return userId;
    }


    private static void safeUpdate(
            String newValue,
            String currentValue,
            Consumer<String> updater,
            Supplier<RuntimeException> exceptionSupplier
    ) {
        if (newValue != null) {
            if (newValue.isBlank()) {
                throw exceptionSupplier.get();
            }
            if (!newValue.equals(currentValue)) {
                updater.accept(newValue);
            }
        }
    }

}
