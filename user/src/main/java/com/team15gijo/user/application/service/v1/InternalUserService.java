package com.team15gijo.user.application.service.v1;

import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.domain.repository.UserRepository;
import com.team15gijo.user.infrastructure.persistence.querydsl.UserQueryDslRepository;
import com.team15gijo.user.presentation.dto.v1.UserSearchResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalUserService {

    private final UserQueryDslRepository userQueryDslRepository;

    @Transactional(readOnly = true)
    public List<UserSearchResponseDto> searchUsers(String keyword, Long userId, String nickname, String region, Long lastUserId, int size) {
        List<UserEntity> users;

        users = userQueryDslRepository.searchUsers(keyword, userId, nickname, region, lastUserId, size);
        System.out.println(users.size());
        return users.stream()
                .map(UserSearchResponseDto::from)
                .toList();

    }

}
