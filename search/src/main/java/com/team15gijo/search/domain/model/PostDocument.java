package com.team15gijo.search.domain.model;

import com.team15gijo.search.infrastructure.kafka.dto.v1.PostElasticsearchRequestDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostUpdatedEventDto;
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
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

@Document(indexName = "posts", writeTypeHint = WriteTypeHint.FALSE)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setting(settingPath = "elastic/post-setting.json")
@Mapping(mappingPath = "elastic/post-mapping.json")
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

    @Field(type = FieldType.Text, analyzer = "nori")
    private String region;

    @Field(type = FieldType.Integer, index = false)
    private int views;

    @Field(type = FieldType.Integer, index = false)
    private int commentCount;

    @Field(type = FieldType.Integer, index = false)
    private int likeCount;

    @Field(type=FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis")
    private LocalDateTime createdAt;

    public static PostDocument from(PostElasticsearchRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("PostElasticsearchRequestDto cannot be null");
        }
        return PostDocument.builder()
                .postId(dto.getPostId())
                .username(dto.getUsername())
                .postContent(dto.getPostContent())
                .hashtags(dto.getHashtags())
                .region(dto.getRegion())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    public void updateFeed(PostUpdatedEventDto dto) {
        this.username = dto.getUsername();
        this.region = dto.getRegion();
        this.postContent = dto.getPostContent();
        this.hashtags = dto.getHashtags();
        this.views = dto.getViews();
        this.commentCount = dto.getCommentCount();
        this.likeCount = dto.getLikeCount();
        this.createdAt = dto.getCreatedAt();
    }

    public void updateViews(int views) {
        this.views = views;
    }
    public void updateCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    public void updateLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
