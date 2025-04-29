package com.team15gijo.follow.infrastructure.kafka.consumer.v2;

import com.team15gijo.follow.infrastructure.kafka.dto.v2.FollowEventDto;

public interface FollowConsumerService {

    void handleFollowCreated(FollowEventDto followEventDto);

    void handleFollowDeleted(FollowEventDto followEventDto);
}
