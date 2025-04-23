package com.team15gijo.search.infrastructure.client.user;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.search.application.dto.v2.UserSearchResponseDto;
import com.team15gijo.search.infrastructure.config.FeignConfig;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/internal/api/v1/users/search")
    ApiResponse<List<UserSearchResponseDto>> searchUsers(
            @RequestParam String keyword,
            @RequestParam Long userId,
            @RequestParam String nickname,
            @RequestParam String region,
            @RequestParam(required = false) Long lastUserId,
            @RequestParam int size);
}
