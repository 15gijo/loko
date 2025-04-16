package com.team15gijo.auth.application.dto.v1;

public record AuthLoginResponseCommand(
        //jwt 헤더 내용
        Long userId,
        String roleName,
        String nickname,
        String region
) {

}
