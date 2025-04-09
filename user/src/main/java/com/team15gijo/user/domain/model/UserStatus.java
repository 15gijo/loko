package com.team15gijo.user.domain.model;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.user.domain.exception.UserDomainExceptionCode;
import lombok.Getter;

@Getter
public enum UserStatus {

    BLOCKED("차단 유저"),
    ACTIVE("활성화 유저");

    private final String userStatusName;

    UserStatus(String userStatusName) {
        this.userStatusName = userStatusName;
    }

    public static UserStatus fromUserStatusName(String userStatusName) {
        for (UserStatus userStatus : UserStatus.values()) {
            if (userStatus.userStatusName.equals(userStatusName)) {
                return userStatus;
            }
        }
        throw new CustomException(UserDomainExceptionCode.USER_TYPE_NOT_FOUND);
    }
}
