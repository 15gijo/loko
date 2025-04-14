package com.team15gijo.chat.infrastructure.client.v1.user;

import com.team15gijo.chat.infrastructure.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserClient {

    /**
     * User 서비스에서 nickname 존재여부 확인 - Feign Client
     * 채팅방 생성 시, 상대방 계정 조회하는지 nickname 으로 판단
     */
    @GetMapping("/internal/api/v1/users/{nickname}")
    Long getUserIdByNickname(@PathVariable("nickname") String nickname);
}
