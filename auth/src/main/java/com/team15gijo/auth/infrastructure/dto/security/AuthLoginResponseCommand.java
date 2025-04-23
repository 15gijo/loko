package com.team15gijo.auth.infrastructure.dto.security;

public record AuthLoginResponseCommand(
        //jwt 헤더 내용
        Long userId,
        String roleName,
        String nickname,
        String region
) {

}
