package com.team15gijo.feed.application.service.v1;

import com.team15gijo.feed.infrastructure.kafka.dto.v1.CommentCreatedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.CommentDeletedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostCreatedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostDeletedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostUpdatedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostViewedEventDto;

public interface FeedEventService {
    void handlePostCreated(PostCreatedEventDto dto);
    void handlePostUpdated(PostUpdatedEventDto dto);
    void handlePostDeleted(PostDeletedEventDto dto);
    void handlePostViewed(PostViewedEventDto dto);
    void handlePostCommented(CommentCreatedEventDto dto);
    void handlePostCommentDeleted(CommentDeletedEventDto dto);
}
