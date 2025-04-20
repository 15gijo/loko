package com.team15gijo.search.domain.model;

import com.team15gijo.search.infrastructure.kafka.dto.PostElasticsearchRequestDto;
import com.team15gijo.search.infrastructure.kafka.dto.UserElasticsearchRequestDto;
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

@Document(indexName = "users", writeTypeHint = WriteTypeHint.FALSE)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setting(settingPath = "elastic/user-setting.json")
@Mapping(mappingPath = "elastic/user-mapping.json")
@Builder
public class UserDocument {

    @Id
    private Long userId;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String username;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String nickname;

    // 1. index = false : 검색 인덱스에 포함하지 않고 단순 조회용 데이터
    // 2. docValues = false : 정렬이나 집계에도 사용 안함
    @Field(type = FieldType.Text, index = false)
    private String profile;

    @Field(type = FieldType.Text)
    private String region;

    public static UserDocument from(UserElasticsearchRequestDto dto) {
        return UserDocument.builder()
                .userId(dto.getUserId())
                .username(dto.getUsername())
                .nickname(dto.getNickname())
                .profile(dto.getProfile())
                .region(dto.getRegion())
                .build();
    }
}
