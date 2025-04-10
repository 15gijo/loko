package com.team15gijo.auth.domain.model;

import com.team15gijo.auth.domain.exception.AuthDomainExceptionCode;
import com.team15gijo.common.exception.CustomException;

public enum Role {
    USER("일반 유저"),
    MANAGER("매니저"),
    ADMIN("관리자");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public static Role fromRoleName(String roleName) {
        for (Role roles : Role.values()) {
            if (roles.roleName.equalsIgnoreCase(roleName)) {
                return roles;
            }
        }
        throw new CustomException(AuthDomainExceptionCode.ROLE_TYPE_NOT_FOUND);
    }
}
