package com.team15gijo.post.presentation.dto.v2;


import com.team15gijo.post.domain.model.v2.PostLikeV2;
import lombok.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikeListResponseDtoV2 {
    private UUID postId;
    private int totalLikes;
    private List<String> likes; // 좋아요를 누른 userId 문자열 리스트

    public static PostLikeListResponseDtoV2 from(UUID postId, List<PostLikeV2> likes) {
        List<String> likeUserIds = likes.stream()
                .map(like -> String.valueOf(like.getUserId()))
                .collect(Collectors.toList());
        return PostLikeListResponseDtoV2.builder()
                .postId(postId)
                .totalLikes(likeUserIds.size())
                .likes(likeUserIds)
                .build();
    }
}
