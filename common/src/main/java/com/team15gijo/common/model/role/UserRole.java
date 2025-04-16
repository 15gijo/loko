package com.team15gijo.common.model.role;

import com.team15gijo.common.exception.CommonExceptionCode;
import com.team15gijo.common.exception.CustomException;
import lombok.Getter;

@Getter
public enum UserRole {
    USER("일반 유저", 1),
    MANAGER("매니저", 2),
    ADMIN("관리자", 3);

    private final String roleName;
    private final int roleLevel;

    UserRole(String roleName, int roleLevel) {
        this.roleName = roleName;
        this.roleLevel = roleLevel;
    }

    public static UserRole fromRoleName(String roleName) {
        for (UserRole role : UserRole.values()) {
            if (role.name().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new CustomException(CommonExceptionCode.ROLE_TYPE_NOT_FOUND);
    }

    public boolean checkUserRoleLevel(UserRole requestRole) {
        return this.roleLevel >= requestRole.roleLevel;
    }
}
