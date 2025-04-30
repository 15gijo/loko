package com.team15gijo.user.infrastructure.kafka.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FollowEventDto {

    private Long followerId;
    private Long followeeId;
    private FollowType followType;
    private LocalDateTime timeStamp;

    public enum FollowType {
        FOLLOW, UNFOLLOW
    }
}

