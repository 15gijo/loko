package com.team15gijo.notification.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/internal/api/v1/users/{nickname}")
    Long getUserIdByNickname(@PathVariable("nickname") String nickname);
}
