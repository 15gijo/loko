package com.team15gijo.user.infrastructure.controller.v1;

import com.team15gijo.user.application.service.UserApplicationService;
import com.team15gijo.user.infrastructure.dto.UserFeignInfoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/api/v1/users")
public class UserInternalController {

    private final UserApplicationService userApplicationService;

    @GetMapping("/info")
    public UserFeignInfoResponseDto getUserInfo(@RequestParam("email") String identifier) {
        log.info("user-service 연결={}", identifier);
        return userApplicationService.getUserInfo(identifier);
    }

}
