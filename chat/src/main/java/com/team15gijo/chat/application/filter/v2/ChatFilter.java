package com.team15gijo.chat.application.filter.v2;

import com.team15gijo.chat.application.config.v2.ChatProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatFilter {

    private final ChatProperties chatProperties;

    /*
     * 정적 필터링으로 금지어가 포함되어 있으면 true, 포함안되어 있으면 false 리턴
     */
    public boolean validateMessageContent(String content) {
        log.info("validateMessageContent: content={}", content);

        boolean result = false;
        String lowerCaseContent = content.toLowerCase();
        for(String word : chatProperties.getBlacklist()) {
            if(lowerCaseContent.contains(word.toLowerCase())) {
                result = true;
                break;
            }
        }
        return result;
    }
}
