package com.team15gijo.user.application.service.v1;

import com.team15gijo.user.application.service.UserApplicationService;
import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.domain.repository.UserRepository;
import com.team15gijo.user.domain.service.UserDomainService;
import com.team15gijo.user.infrastructure.client.AuthServiceClient;
import com.team15gijo.user.infrastructure.dto.v1.internal.AuthSignUpRequestDto;
import com.team15gijo.user.infrastructure.dto.v1.internal.AuthSignUpResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserSignUpRequestDto;
import com.team15gijo.user.presentation.dto.v1.UserSignUpResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info("유저 서비스 created user: {}", createdUser);
        //인증 서버로 회원가입 알림
        AuthSignUpRequestDto authSignUpRequsetDto =
                AuthSignUpRequestDto.builder()
                        .nickname(createdUser.getNickName())
                        .identifier(createdUser.getEmail())
                        .password(userSignUpRequestDto.password())
                        .loginTypeName("PASSWORD")
                        .build();

        AuthSignUpResponseDto authSignUpResponseDto = authServiceClient.signUp(authSignUpRequsetDto);

        //유저 DB save
        UserEntity savedUser = userRepository.save(createdUser);

        return new UserSignUpResponseDto(
                savedUser.getEmail(),
                savedUser.getNickName(),
                savedUser.getUserName(),
                savedUser.getRegion(),
                savedUser.getProfile());
    }
}
