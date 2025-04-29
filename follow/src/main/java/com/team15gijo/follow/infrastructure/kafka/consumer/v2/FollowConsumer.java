package com.team15gijo.follow.infrastructure.kafka.consumer.v2;

import com.team15gijo.follow.infrastructure.kafka.dto.v2.FollowEventDto;

public interface FollowConsumer {

    void consumeFollowCreated(FollowEventDto followEventDto);

    void consumeFollowDeleted(FollowEventDto followEventDto);

}
