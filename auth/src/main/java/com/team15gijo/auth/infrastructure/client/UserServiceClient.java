package com.team15gijo.auth.infrastructure.client;

import com.team15gijo.auth.infrastructure.config.UserFeignClientConfig;
import com.team15gijo.auth.infrastructure.dto.feign.response.v1.UserFeignInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", path = "/internal/api/v1/users", configuration = UserFeignClientConfig.class)
public interface UserServiceClient {

    @GetMapping("/info")
    UserFeignInfoResponseDto getUserInfo(@RequestParam("email") String identifier);

    @GetMapping("/{userId}/email")
    String getEmailByUserId(@PathVariable("userId") Long userId);
}
