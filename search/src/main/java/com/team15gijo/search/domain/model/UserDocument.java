package com.team15gijo.search.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDocument {
    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Text)
    private String username;

    @Field(type = FieldType.Text)
    private String nickname;

    // 1. index = false : 검색 인덱스에 포함하지 않고 단순 조회용 데이터
    // 2. docValues = false : 정렬이나 집계에도 사용 안함
    @Field(type = FieldType.Text, index = false)
    private String profile;

    @Field(type = FieldType.Text)
    private String region;
}
