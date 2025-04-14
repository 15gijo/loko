package com.team15gijo.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 권한 기반 접근 제어를 위한 커스텀 어노테이션.
 *
 * <p>컨트롤러의 메서드 또는 클래스에 붙여 사용하며,
 * 지정된 역할(Role)에 따라 접근을 허용하거나 차단합니다.</p>
 *
 * <ul>
 *   <li><b>@Target</b> : 어노테이션을 어디에 적용할 수 있는지 정의</li>
 *   <ul>
 *     <li>ElementType.METHOD : 메서드에 적용 가능</li>
 *     <li>ElementType.TYPE : 클래스에 적용 가능</li>
 *   </ul>
 *
 *   <li><b>@Retention</b> : 어노테이션이 언제까지 유지될지 정의</li>
 *   <ul>
 *     <li>RetentionPolicy.RUNTIME : 런타임까지 유지 → Interceptor 등에서 읽을 수 있음</li>
 *   </ul>
 *
 *   <li><b>@Documented</b> : Javadoc 생성 시 표시되도록 설정</li>
 * </ul>
 *
 * @param value 명시적으로 허용할 역할 목록 (예: {"MASTER", "MANAGER"})
 * @param min 최소 권한 (예: "MANAGER") — 해당 권한 이상이면 허용
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RoleGuard {

    String[] value() default {};

    String min() default "";
}
