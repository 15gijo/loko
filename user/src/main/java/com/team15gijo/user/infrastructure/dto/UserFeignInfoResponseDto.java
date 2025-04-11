package com.team15gijo.user.infrastructure.dto;

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
}
