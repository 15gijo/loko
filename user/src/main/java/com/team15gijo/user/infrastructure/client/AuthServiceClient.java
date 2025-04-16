package com.team15gijo.user.infrastructure.client;

import com.team15gijo.user.infrastructure.config.AuthFeignClientConfig;
import com.team15gijo.user.infrastructure.dto.v1.internal.AuthSignUpRequestDto;
import com.team15gijo.user.infrastructure.dto.v1.internal.AuthSignUpUpdateUserIdRequestDto;
import com.team15gijo.user.presentation.dto.v1.internal.request.AuthIdentifierUpdateRequestDto;
import com.team15gijo.user.presentation.dto.v1.internal.request.AuthPasswordUpdateRequestDto;
import com.team15gijo.user.presentation.dto.v1.request.UserEmailUpdateRequestDto;
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
