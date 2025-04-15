package com.team15gijo.user.application.dto.v1;

import com.team15gijo.user.domain.model.UserStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminUserSearchCommand {

    private final Long userId;
    private final String username;
    private final String nickname;
    private final String email;
    private final UserStatus userStatus;
    private final String region;

}
