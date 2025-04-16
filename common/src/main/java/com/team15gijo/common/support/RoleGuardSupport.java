package com.team15gijo.common.support;

import com.team15gijo.common.model.role.UserRole;
import java.util.Arrays;

public interface RoleGuardSupport {

    default boolean checkUserRoleLevel(String headerUserRoleName, String requestRoleName) {
        try {
            UserRole header = UserRole.fromRoleName(headerUserRoleName);
            System.out.println("header-role-name: " + header);
            UserRole request = UserRole.fromRoleName(requestRoleName);
            System.out.println("request-role-name: " + request);
            System.out.println(header.checkUserRoleLevel(request));
            return header.checkUserRoleLevel(request);
        } catch (Exception e) {
            return false;
        }
    }

    default boolean isEqualRoleName(String headerUserRoleName, String[] requestRoleNames) {
        try {
            UserRole header = UserRole.fromRoleName(headerUserRoleName);

            return Arrays.stream(requestRoleNames)
                    .map(UserRole::fromRoleName)
                    .anyMatch(requestRoleName -> requestRoleName == header);
        } catch (Exception e) {
            return false;
        }
    }

}
