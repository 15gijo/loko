package com.team15gijo.search.infrastructure.client.post;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostSearchResponseDto {

    private UUID postId;
    private long userId;
    private String username;
    private String region;
    private String content;
    private List<String> hashtags;
    private int views;

}
