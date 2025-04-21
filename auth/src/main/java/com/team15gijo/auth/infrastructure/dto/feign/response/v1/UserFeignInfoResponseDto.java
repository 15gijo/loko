package com.team15gijo.auth.infrastructure.dto.feign.response.v1;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserFeignInfoResponseDto {

    Long userId;
    String nickname;
    String region;
    String userStatusName;
}
