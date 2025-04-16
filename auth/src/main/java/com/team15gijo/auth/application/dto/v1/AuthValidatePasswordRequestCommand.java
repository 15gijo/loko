package com.team15gijo.auth.application.dto.v1;

public record AuthValidatePasswordRequestCommand(
        String rawPassword,
        String encodedPassword
) {

}
