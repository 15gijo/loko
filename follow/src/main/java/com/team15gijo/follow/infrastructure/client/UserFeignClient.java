package com.team15gijo.follow.infrastructure.client;

import com.team15gijo.follow.infrastructure.config.UserFeignClientConfig;
import com.team15gijo.follow.infrastructure.dto.response.v2.UserInfoFollowResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "internal/api/v1/users", configuration = UserFeignClientConfig.class)
public interface UserFeignClient {

    @GetMapping("/{userId}/following")
    UserInfoFollowResponseDto getUserInfoFollowing(@PathVariable("userId") Long userId);

    @GetMapping("/{userId}/follower")
    UserInfoFollowResponseDto getUserInfoFollower(@PathVariable("userId") Long userId);

}
