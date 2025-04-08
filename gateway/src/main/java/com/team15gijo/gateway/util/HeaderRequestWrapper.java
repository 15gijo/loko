package com.team15gijo.gateway.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

/**
 * HttpServletRequest를 감싸서 커스텀 헤더를 추가하거나 기존 헤더를 오버라이드할 수 있도록 도와주는 래퍼 클래스입니다.
 *
 * <p>
 * 예를 들어, 인증 필터에서 JWT 토큰을 파싱한 후 사용자 정보를
 * "X-User-Id" 같은 헤더로 추가하고, 이후 필터 체인 또는 다른 MSA 서비스에서 이를 사용할 수 있도록 할 때 사용됩니다.
 * </p>
 */
public class HeaderRequestWrapper extends HttpServletRequestWrapper {
    private final Map<String, String> customHeaders = new HashMap<>();

    public HeaderRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 커스텀 헤더, 동일한 이름의 기존 헤더는 덮어씁니다.
     *
     * @param name
     * @param value
     * @return
     */
    public HeaderRequestWrapper addHeader(String name, String value) {
        customHeaders.put(name, value);
        return this;
    }

    @Override
    public String getHeader(String name) {
        String headerValue = customHeaders.get(name);
        return headerValue == null ? super.getHeader(name) : headerValue;
    }

    /**
     * 요청에 포함된 모든 헤더 이름을 반환합니다.
     *
     * @return
     */
    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> headerNames = new HashSet<>(customHeaders.keySet());
        Enumeration<String> originalHeaderNames = super.getHeaderNames();
        while (originalHeaderNames.hasMoreElements()) {
            headerNames.add(originalHeaderNames.nextElement());
        }
        return Collections.enumeration(headerNames);
    }

    /**
     * 특정 이름의 헤더 값들을 반환합니다.
     * 커스텀 헤더가 존재하면 해당 값만 반환합니다.
     *
     * @param name
     * @return
     */
    @Override
    public Enumeration<String> getHeaders(String name) {
        String headerValue = customHeaders.get(name);
        if (headerValue != null) {
            return Collections.enumeration(List.of(headerValue));
        }
        return super.getHeaders(name);
    }

}
