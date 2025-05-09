package com.team15gijo.user.application.service.v2;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.user.application.dto.v1.AdminUserSearchCommand;
import com.team15gijo.user.application.dto.v2.KakaoMapRegionInfoCommand;
import com.team15gijo.user.application.dto.v2.OAuthUserProfile;
import com.team15gijo.user.application.exception.UserApplicationExceptionCode;
import com.team15gijo.user.application.service.UserApplicationService;
import com.team15gijo.user.domain.exception.UserDomainExceptionCode;
import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.domain.model.UserRegionEntity;
import com.team15gijo.user.domain.model.UserStatus;
import com.team15gijo.user.domain.repository.UserRegionRepositroy;
import com.team15gijo.user.domain.repository.UserRepository;
import com.team15gijo.user.domain.service.UserDomainService;
import com.team15gijo.user.infrastructure.client.AuthServiceClient;
import com.team15gijo.user.infrastructure.dto.UserFeignInfoResponseDto;
import com.team15gijo.user.infrastructure.dto.request.v1.AuthIdentifierUpdateRequestDto;
import com.team15gijo.user.infrastructure.dto.request.v1.AuthPasswordUpdateRequestDto;
import com.team15gijo.user.infrastructure.dto.request.v1.AuthSignUpRequestDto;
import com.team15gijo.user.infrastructure.dto.request.v1.AuthSignUpUpdateUserIdRequestDto;
import com.team15gijo.user.infrastructure.dto.request.v1.OAuthAuthSignUpRequestDto;
import com.team15gijo.user.infrastructure.jwt.JwtProvider;
import com.team15gijo.user.infrastructure.kafka.dto.UserElasticsearchRequestDto;
import com.team15gijo.user.infrastructure.kafka.service.KafkaProducerService;
import com.team15gijo.user.infrastructure.kakao.KakaoMapService;
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
import io.jsonwebtoken.Claims;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
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
    private final UserRegionRepositroy userRegionRepository;
    private final AuthServiceClient authServiceClient;
    private final KafkaProducerService producerService;
    private final KakaoMapService kakaoMapService;
    private final GeometryFactory geometryFactory;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public UserSignUpResponseDto createUser(UserSignUpRequestDto userSignUpRequestDto) {
        //카카오맵 주소 받기
        KakaoMapRegionInfoCommand kakaoMapRegionInfoCommand = kakaoMapService.getRegionInfo(
                userSignUpRequestDto.region());

        //좌표 받기
        Point location = geometryFactory.createPoint(
                new Coordinate(kakaoMapRegionInfoCommand.longitude(),
                        kakaoMapRegionInfoCommand.latitude()));

        //지역 디비 등록
        UserRegionEntity userRegionEntity = userRegionRepository.save(
                UserRegionEntity.builder()
                        .regionCode(kakaoMapRegionInfoCommand.regionCode())
                        .regionName(kakaoMapRegionInfoCommand.regionName())
                        .location(location)
                        .build()
        );

        //유저 생성
        UserEntity createdUser = userDomainService.createUser(
                userSignUpRequestDto.withRegion(userRegionEntity.getRegionName()),
                userRegionEntity
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
        try {
            producerService.sendUserCreate(UserElasticsearchRequestDto.from(savedUser));
            log.info("user 정보 검색서버로 kafka 전송 완료");
        } catch (Exception e) {
            log.error("user 정보 검색서버로 kafka 전송 실패, userId: {}", savedUser.getId(), e);
        }

        return new UserSignUpResponseDto(
                savedUser.getEmail(),
                savedUser.getNickname(),
                savedUser.getUsername(),
                savedUser.getRegion(),
                savedUser.getProfile(),
                savedUser.getRegionId().getId());
    }

    @Override
    @Transactional
    public UserSignUpResponseDto createUserOauth(
            UserOAuthSignUpRequestDto userOAuthSignUpRequestDto) {
        //회원가입 토큰 받기, 레디스에서 꺼내기
        String signupToken = userOAuthSignUpRequestDto.signUpToken();
        Claims claims = jwtProvider.parseToken(signupToken);

        //dto 변환
        OAuthUserProfile oAuthUserProfile = OAuthUserProfile.builder()
                .provider(claims.get("provider", String.class))
                .providerId(claims.get("providerId", String.class))
                .email(claims.get("email", String.class))
                .name(claims.get("name", String.class))
                .nickname(claims.get("nickname", String.class))
                .profileImageUrl(claims.get("profileImageUrl", String.class))
                .build();

        //카카오맵 주소 받기
        KakaoMapRegionInfoCommand kakaoMapRegionInfoCommand = kakaoMapService.getRegionInfo(
                userOAuthSignUpRequestDto.region());

        //좌표 받기
        Point location = geometryFactory.createPoint(
                new Coordinate(kakaoMapRegionInfoCommand.longitude(),
                        kakaoMapRegionInfoCommand.latitude()));

        //지역 디비 등록
        UserRegionEntity userRegionEntity = userRegionRepository.save(
                UserRegionEntity.builder()
                        .regionCode(kakaoMapRegionInfoCommand.regionCode())
                        .regionName(kakaoMapRegionInfoCommand.regionName())
                        .location(location)
                        .build()
        );

        //유저 생성
        UserEntity createdUser = userDomainService.createUserOauth(
                oAuthUserProfile,
                userOAuthSignUpRequestDto.username(),
                userRegionEntity.getRegionName(),
                userRegionEntity
        );

        //인증 서버로 회원가입 알림
        OAuthAuthSignUpRequestDto oAuthAuthSignUpRequestDto =
                OAuthAuthSignUpRequestDto.builder()
                        .nickname(createdUser.getNickname())
                        .identifier(createdUser.getEmail())
                        .password("OAUTH")
                        .loginTypeName(oAuthUserProfile.getProvider().toUpperCase())
                        .oauthId(oAuthUserProfile.getProviderId())
                        .build();

        UUID authId = authServiceClient.signUpOAuth(oAuthAuthSignUpRequestDto);

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
        try {
            producerService.sendUserCreate(UserElasticsearchRequestDto.from(savedUser));
            log.info("user 정보 검색서버로 kafka 전송 완료");
        } catch (Exception e) {
            log.error("user 정보 검색서버로 kafka 전송 실패, userId: {}", savedUser.getId(), e);
        }

        return new UserSignUpResponseDto(
                savedUser.getEmail(),
                savedUser.getNickname(),
                savedUser.getUsername(),
                savedUser.getRegion(),
                savedUser.getProfile(),
                savedUser.getRegionId().getId());
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
    public List<UserAndRegionInfoFollowResponseDto> getUserAndRegionInfoForRecommend(
            Long myUserId,
            List<Long> candidateUserIds) {
        UserEntity user = userRepository.findById(myUserId)
                .orElseThrow(() -> new CustomException(UserDomainExceptionCode.USER_NOT_FOUND));

        UUID regionId = user.getRegionId().getId();

        UserRegionEntity myRegion = userRegionRepository.findById(regionId)
                .orElseThrow(() -> new CustomException(UserDomainExceptionCode.REGION_NOT_FOUND));

        Point location = myRegion.getLocation();

        return userRepository.findUserAndRegionInfos(location, candidateUserIds);
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

    //internal - kafka
    @Override
    @Transactional
    public void increaseFollowCount(Long followerId, Long followeeId) {
        log.info(
                "[UserApplicationService-kafka] followerId={} followingCount +1, followeeId={} followerCount +1",
                followerId, followeeId);
        int updatedFollowings = userRepository.incrementFollowingCount(followerId);
        int updatedFollowers = userRepository.incrementFollowerCount(followeeId);

        if (updatedFollowings == 0 || updatedFollowers == 0) {
            throw new RuntimeException(
                    "[Kafka-Follow] 유효하지 않은 유저 ID - followerId=" + followerId + ", followeeId="
                            + followeeId);
        }
    }

    //internal - kafka
    @Override
    @Transactional
    public void decreaseFollowCount(Long followerId, Long followeeId) {
        log.info(
                "[UserApplicationService-kafka] followerId={} followingCount -1, followeeId={} followerCount -1",
                followerId, followeeId);
        int updatedFollowings = userRepository.decrementFollowingCount(followerId);
        int updatedFollowers = userRepository.decrementFollowerCount(followeeId);

        if (updatedFollowings == 0 || updatedFollowers == 0) {
            throw new RuntimeException(
                    "[Kafka-Follow] 유효하지 않은 유저 ID - followerId=" + followerId + ", followeeId="
                            + followeeId);
        }
    }

    //internal - redis scheduler 용
    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }


    private static int getIndex(String kakaoMapRegion) {
        int indexGu = kakaoMapRegion.indexOf("구");
        int indexGoon = kakaoMapRegion.indexOf("군");
        int index = -1;
        if (indexGu != -1 && indexGoon != -1) {
            throw new CustomException(UserApplicationExceptionCode.INVALID_ADDRESS);
        } else if (indexGu != -1) {
            index = indexGu;
        } else if (indexGoon != -1) {
            index = indexGoon;
        }
        if (index == -1) {
            //fallback 나중에
            throw new CustomException(UserApplicationExceptionCode.INVALID_ADDRESS);
        }
        return index;
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
