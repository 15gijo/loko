//package com.team15gijo.user.presentation.dto.response;
//
//import com.team15gijo.user.domain.model.UserEntity;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Getter
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//public class UserSearchResponseDto {
//    private Long userId;
//    private String username;
//    private String nickname;
//    private String profile;
//    private String region;
//
//    public static UserSearchResponseDto from(UserEntity user) {
//        return UserSearchResponseDto.builder()
//                .userId(user.getId())
//                .username(user.getUsername())
//                .nickname(user.getNickname())
//                .profile(user.getProfile())
//                .region(user.getRegion())
//                .build();
//    }
//}
