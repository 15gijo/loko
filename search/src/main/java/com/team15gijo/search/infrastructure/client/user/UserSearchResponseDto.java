package com.team15gijo.search.infrastructure.client.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchResponseDto {
    private Long userId;
    private String username;
    private String nickname;
    private String profile;
    private String region;
}
