package com.team15gijo.user.infrastructure.client;

import com.team15gijo.user.infrastructure.config.AuthFeignClientConfig;
import com.team15gijo.user.infrastructure.dto.request.v1.AuthSignUpRequestDto;
import com.team15gijo.user.infrastructure.dto.request.v1.AuthSignUpUpdateUserIdRequestDto;
import com.team15gijo.user.infrastructure.dto.request.v1.AuthIdentifierUpdateRequestDto;
import com.team15gijo.user.infrastructure.dto.request.v1.AuthPasswordUpdateRequestDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", path = "internal/api/v1/auth", configuration = AuthFeignClientConfig.class)
public interface AuthServiceClient {

    @PostMapping("/signup")
    UUID signUp(@RequestBody AuthSignUpRequestDto authSignUpRequestDto);

    @PutMapping("/signup-updateUserId")
    void updateId(@RequestBody AuthSignUpUpdateUserIdRequestDto authSignUpUpdateUserIdRequestDto);

    @PostMapping("/identifier-update")
    void updateIdentifier(@RequestBody AuthIdentifierUpdateRequestDto authIdentifierUpdateRequestDto);

    @PostMapping("/password-update")
    void updatePassword(@RequestBody AuthPasswordUpdateRequestDto authPasswordUpdateRequestDto);
}
