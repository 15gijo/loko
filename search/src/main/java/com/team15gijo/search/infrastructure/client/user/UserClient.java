package com.team15gijo.search.infrastructure.client.user;

import com.team15gijo.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/v1/users/search")
    ApiResponse<Page<UserSearchResponseDto>> searchUsers(@RequestParam String keyword);
}
