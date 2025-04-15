package com.team15gijo.user.infrastructure.dto.v1.internal;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchResponseDto {

    private String username;
    private String nickname;
    private String profile;
    private String region;
}

