package com.team15gijo.user.presentation.controller.v1;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.user.application.service.UserApplicationService;
import com.team15gijo.user.application.service.v1.InternalUserService;
import com.team15gijo.user.infrastructure.dto.UserFeignInfoResponseDto;
import com.team15gijo.user.presentation.dto.internal.response.v1.UserAndRegionInfoFollowResponseDto;
import com.team15gijo.user.presentation.dto.internal.response.v1.UserInfoFollowResponseDto;
import com.team15gijo.user.presentation.dto.internal.response.v1.UserSearchResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "유저 검색 internal controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/api/v1/users")
public class UserInternalController {

    private final UserApplicationService userApplicationService;
    private final InternalUserService internalUserService;

    //auth -> user
    //유저 조회
    @GetMapping("/info")
    public UserFeignInfoResponseDto getUserInfo(@RequestParam("email") String identifier) {
        log.info("auth - user 연결={}", identifier);
        return userApplicationService.getUserInfo(identifier);
    }

    //auth -> user
    //유저 이메일 조회
    @GetMapping("/{userId}/email")
    String getEmailByUserId(@PathVariable("userId") Long userId) {
        log.info("auth - user 연결={}", userId);
        return userApplicationService.getEmailByUserId(userId);
    }

    //chat -> user
    //유저 닉네임 존재 여부
    @GetMapping("/{nickname}")
    public Long getUserIdByNickname(@PathVariable("nickname") String nickname) {
        log.info("chat - user 연결={}", nickname);
        return userApplicationService.getUserIdByNickname(nickname);
    }


    //유저 검색
    @GetMapping("/search")
    public ApiResponse<List<UserSearchResponseDto>> searchUsers(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "nickname") String nickname,
            @RequestParam(name = "region") String region,
            @RequestParam(required = false) Long lastUserId,
            @RequestParam(defaultValue = "10") int size) {
        log.info("키워드 : {}, 닉네임 : {}, 지역 : {}", keyword, nickname, region);
        List<UserSearchResponseDto> users = internalUserService.searchUsers(keyword, userId,
                nickname, region, lastUserId, size);
        return ApiResponse.success("유저 검색 성공", users);
    }


    //follow -> user
    @GetMapping("/{userId}/following")
    public UserInfoFollowResponseDto getUserInfoFollowing(@PathVariable("userId") Long userId) {
        log.info("follow - user 연결={}", userId);
        return userApplicationService.getUserInfoForFollow(userId);
    }

    //follow -> user
    @GetMapping("/{userId}/follower")
    public UserInfoFollowResponseDto getUserInfoFollower(@PathVariable("userId") Long userId) {
        log.info("follow - user 연결={}", userId);
        return userApplicationService.getUserInfoForFollow(userId);
    }

    //follow(recommend) -> user
    @PostMapping("/region-infos")
    public List<UserAndRegionInfoFollowResponseDto> getUserAndRegionInfoForRecommend(
            @RequestBody List<Long> candidateUserIds) {
        log.info("follow(recommend) - user 연결={}", candidateUserIds);
        return userApplicationService.getUserAndRegionInfoForRecommend(candidateUserIds);
    }

}
