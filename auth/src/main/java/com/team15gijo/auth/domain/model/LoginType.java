package com.team15gijo.auth.domain.model;

import com.team15gijo.auth.domain.exception.AuthDomainExceptionCode;
import com.team15gijo.common.exception.CustomException;

public enum LoginType {
    PASSWORD("일반 로그인"),
    SOCIAL("소셜 로그인"); //OAuth 구현시 세분화 필요

    private final String loginTypeName;

    LoginType(String loginTypeName) {
        this.loginTypeName = loginTypeName;
    }

    public static LoginType fromLoginTypeName(String loginTypeName) {
        for (LoginType loginType : LoginType.values()) {
            if (loginType.name().equalsIgnoreCase(loginTypeName)) {
                return loginType;
            }
        }
        throw new CustomException(AuthDomainExceptionCode.LOGIN_TYPE_NOT_FOUND);
    }
}
