package com.team15gijo.post.infrastructure.kafka.dto.v1;

public enum EventType {
    POST_CREATED,
    POST_UPDATED,
    POST_DELETED,
    POST_VIEWED,
    POST_LIKED, //추가 예정
    POST_UNLIKED, //추가 예정
    COMMENT_CREATED,
    COMMENT_DELETED
}
