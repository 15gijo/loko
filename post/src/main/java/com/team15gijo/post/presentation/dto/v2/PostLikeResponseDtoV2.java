package com.team15gijo.post.presentation.dto.v2;


import com.team15gijo.post.domain.model.v2.PostLikeV2;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikeResponseDtoV2 {
    private UUID postId;
    private long userId;
    private LocalDateTime createdAt;

    public static PostLikeResponseDtoV2 from(PostLikeV2 like) {
        return PostLikeResponseDtoV2.builder()
                .postId(like.getPost().getPostId())
                .userId(like.getUserId())
                .createdAt(like.getCreatedAt())
                .build();
    }
}
