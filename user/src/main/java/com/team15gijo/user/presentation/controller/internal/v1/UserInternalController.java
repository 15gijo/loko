package com.team15gijo.user.presentation.controller.internal.v1;

import com.team15gijo.user.application.service.UserApplicationService;
import com.team15gijo.user.infrastructure.dto.UserFeignInfoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/api/v1/users")
public class UserInternalController {

    private final UserApplicationService userApplicationService;

    //auth -> user
    //유저 조회
    @GetMapping("/info")
    public UserFeignInfoResponseDto getUserInfo(@RequestParam("email") String identifier) {
        log.info("user-service 연결={}", identifier);
        return userApplicationService.getUserInfo(identifier);
    }

    //chat -> user
    //유저 닉네임 존재 여부
    @GetMapping("/{nickname}")
    public Long getUserIdByNickname(@PathVariable("nickname") String nickname) {
        return null;
    }


    //post -> user
    //유저 조건 전체 폐이징 조회
    @GetMapping("/api/v1/users/search")
    public ApiResponse<Page<UserSearchResponseDto>> searchUsers(@RequestParam String keyword) {
        return null;
    }


}
