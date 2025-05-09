package com.team15gijo.chat.application.config.v2;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chat")
public class ChatProperties {
    /*
     * application.yml의 chat.blacklist 바인딩하여 키워드 사용
     */
    private List<String> blacklist;

    public List<String> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<String> blacklist) {
        this.blacklist = blacklist;
    }
}
