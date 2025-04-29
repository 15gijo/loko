package com.team15gijo.follow.infrastructure.kafka.producer.v2;

public interface FollowProducerService {

    void sendFollowCreated(Long followerId, Long followeeId);

    void sendFollowDeleted(Long followerId, Long followeeId);
}
