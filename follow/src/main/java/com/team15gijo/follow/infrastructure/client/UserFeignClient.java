package com.team15gijo.follow.infrastructure.client;

import com.team15gijo.follow.infrastructure.config.UserFeignClientConfig;
import com.team15gijo.follow.infrastructure.dto.request.UserAndRegionInfoRequestDto;
import com.team15gijo.follow.infrastructure.dto.response.v2.UserAndRegionInfoFollowResponseDto;
import com.team15gijo.follow.infrastructure.dto.response.v2.UserInfoFollowResponseDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", path = "internal/api/v1/users", configuration = UserFeignClientConfig.class)
public interface UserFeignClient {

    @GetMapping("/{userId}/following")
    UserInfoFollowResponseDto getUserInfoFollowing(@PathVariable("userId") Long userId);

    @GetMapping("/{userId}/follower")
    UserInfoFollowResponseDto getUserInfoFollower(@PathVariable("userId") Long userId);

    @PostMapping("/region-infos")
    List<UserAndRegionInfoFollowResponseDto> getUserAndRegionInfo(
            @RequestBody UserAndRegionInfoRequestDto userAndRegionInfoRequestDto);
}
