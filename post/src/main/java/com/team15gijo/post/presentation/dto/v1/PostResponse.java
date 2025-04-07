package com.team15gijo.post.presentation.dto.v1;

import com.team15gijo.post.domain.model.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PostResponse {
    private UUID postId;
    private long userId;
    private String username;
    private String region;
    private String content;
    private List<String> hashtags;
    private int views;

    public PostResponse(Post post) {
        this.postId = post.getPostId();
        this.userId = post.getUserId();
        this.username = post.getUsername();
        this.region = post.getRegion();
        this.content = post.getContent();
        this.hashtags = post.getHashtags();
        this.views = post.getViews();
    }
}
