package com.team15gijo.user.application.service.v1;

import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.infrastructure.dto.v1.internal.UserSearchResponseDto;
import com.team15gijo.user.infrastructure.persistence.querydsl.UserQueryDslRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "유저 검색 Internal Service")
@Service
@RequiredArgsConstructor
public class InternalUserService {

    private final UserQueryDslRepository userQueryDslRepository;

    @Transactional(readOnly = true)
    public List<UserSearchResponseDto> searchUsers(String keyword, Long userId, String nickname,
            String region, Long lastUserId, int size) {
        List<UserEntity> users;
        log.info("유저 검색 QueryDsl 시작");
        users = userQueryDslRepository.searchUsers(keyword, userId, nickname, region, lastUserId,
                size);
        log.info("유저 검색 QueryDsl 종료");
        return users.stream()
                .map(UserSearchResponseDto::from)
                .toList();

    }

}
