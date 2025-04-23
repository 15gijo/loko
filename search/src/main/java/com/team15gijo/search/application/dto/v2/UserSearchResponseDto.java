package com.team15gijo.search.application.dto.v2;

import com.team15gijo.search.domain.model.UserDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSearchResponseDto {
    private Long userId;
    private String username;
    private String nickname;
    private String profile;
    private String region;

    public static UserSearchResponseDto from(UserDocument user) {
        return UserSearchResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .profile(user.getProfile())
                .region(user.getRegion())
                .build();
    }
}
