package com.team15gijo.user.infrastructure.dto.response.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoMapAddressResponseDto {

    private Meta meta;
    private List<Document> documents;

    @Getter
    public static class Meta {

        @JsonProperty("total_count")
        private int totalCount;
    }

    @Getter
    public static class Document {

        @JsonProperty("address_name")
        private String addressName;
        @JsonProperty("x")
        private String x;
        @JsonProperty("y")
        private String y;

        @JsonProperty("address")
        private Address address;

    }

    @Getter
    public static class Address {

        @JsonProperty("address_name")
        private String addressName;
        @JsonProperty("region_1depth_name")
        private String region_1depth_name;
        @JsonProperty("region_2depth_name")
        private String region_2depth_name;
        @JsonProperty("region_3depth_name")
        private String region_3depth_name;
        @JsonProperty("b_code")
        private String bCode;
    }
}
