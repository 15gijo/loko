package com.team15gijo.comment.application.config.v2;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "comment")
public class CommentProperties {
    /**
    * application.yml의 comment.blacklist 리스트 바인딩
    */
    private List<String> blacklist;

    public List<String> getBlacklist() {
        return blacklist;
    }
    public void setBlacklist(List<String> blacklist) {
        this.blacklist = blacklist;
    }
}
