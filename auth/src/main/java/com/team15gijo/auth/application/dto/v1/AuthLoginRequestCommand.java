package com.team15gijo.auth.application.dto.v1;

import com.team15gijo.auth.domain.model.LoginType;
import com.team15gijo.auth.domain.model.Role;

public record AuthLoginRequestCommand(
        String getPassword,
        String password,
        Role role,
        Long userId,
        String nickname,
        String region,
        LoginType loginType
) {

}
