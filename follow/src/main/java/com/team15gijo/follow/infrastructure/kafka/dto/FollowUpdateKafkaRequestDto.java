package com.team15gijo.follow.infrastructure.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowUpdateKafkaRequestDto {

    private Long userId;

    /**
     *  다른 정보를 보낼게 있는지?
     */

}
