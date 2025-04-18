package com.team15gijo.search.domain.model;

import com.team15gijo.search.infrastructure.client.post.PostSearchResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "posts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDocument {

    @Id
    private UUID postId;

    @Field(type = FieldType.Text)
    private String username;

    @Field(type = FieldType.Text)
    private String postContent;

    @Field(type = FieldType.Text)
    private List<String> hashtags;

//    @Field(type = FieldType.Text, analyzer = "nori")
    @Field(type = FieldType.Text)
    private String region;

    // 생성될 때 저장되어도 값이 변하지 않기 때문에 일단 보류. 추후 쓰기DB와 연동하게 되면 추가
//    @Field(type = FieldType.Integer, index = false)
//    private int views;
//
//    @Field(type = FieldType.Integer, index = false)
//    private int commentCount;
//
//    @Field(type = FieldType.Integer, index = false)
//    private int likeCount;
//
    @Field(type=FieldType.Date)
    private LocalDateTime createdAt;

    public static PostDocument from(PostSearchResponseDto dto) {
        return PostDocument.builder()
                .postId(dto.getPostId())
                .username(dto.getUsername())
                .postContent(dto.getPostContent())
                .hashtags(dto.getHashtags())
                .region(dto.getRegion())
//                .createdAt(dto.getCreatedAt())
                .build();
    }


}
