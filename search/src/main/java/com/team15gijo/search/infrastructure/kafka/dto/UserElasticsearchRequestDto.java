package com.team15gijo.search.infrastructure.kafka.dto;

import com.team15gijo.search.domain.model.UserDocument;
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

    public static UserElasticsearchRequestDto from(UserDocument user) {
        return UserElasticsearchRequestDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .profile(user.getProfile())
                .region(user.getRegion())
                .build();
    }
}
