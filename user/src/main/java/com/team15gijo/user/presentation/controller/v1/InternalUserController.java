package com.team15gijo.user.presentation.controller.v1;

import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.user.application.service.v1.InternalUserService;
import com.team15gijo.user.presentation.dto.v1.UserSearchResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "유저 검색 internal controller")
@RestController
@RequestMapping("/internal/api/v1/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final InternalUserService internalUserService;

    @GetMapping("/search")
    public ApiResponse<List<UserSearchResponseDto>> searchUsers(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "nickname") String nickname,
            @RequestParam(name = "region") String region,
            @RequestParam(required = false) Long lastUserId,
            @RequestParam(defaultValue = "10") int size) {
        log.info("키워드 : {}, 닉네임 : {}, 지역 : {}", keyword, nickname, region);
        List<UserSearchResponseDto> users = internalUserService.searchUsers(keyword, userId, nickname, region, lastUserId, size);
        return ApiResponse.success("유저 검색 성공", users);
    }
}
