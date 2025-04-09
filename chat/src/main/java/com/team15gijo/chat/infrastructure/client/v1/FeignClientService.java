package com.team15gijo.chat.infrastructure.client.v1;

import com.team15gijo.chat.infrastructure.client.v1.user.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeignClientService {

    private final UserClient userClient;

    /**
     * User 서비스에서 nickname 존재여부 확인 - Feign Client
     */
    public Boolean fetchNicknameExists(String receiverNickname) {
        return userClient.nicknameExists(receiverNickname);
    }
}
