package com.team15gijo.user.infrastructure.kafka.dto;

import com.team15gijo.user.domain.model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserElasticsearchRequestDto {

    private Long userId;
    private String username;
    private String nickname;
    private String profile;
    private String region;

    public static UserElasticsearchRequestDto from(UserEntity user) {
        return UserElasticsearchRequestDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .profile(user.getProfile())
                .region(user.getRegion())
                .build();
    }
}