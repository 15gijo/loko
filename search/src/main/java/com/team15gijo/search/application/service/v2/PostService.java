package com.team15gijo.search.application.service.v2;

import com.team15gijo.search.infrastructure.kafka.dto.v2.CommentCreatedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.CommentDeletedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostCreatedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostDeletedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostLikedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostUnlikedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostUpdatedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostViewedEventDto;

public interface PostService {
    void handlePostCreated(PostCreatedEventDto dto);
    void handlePostUpdated(PostUpdatedEventDto dto);
    void handlePostDeleted(PostDeletedEventDto dto);
    void handlePostViewed(PostViewedEventDto dto);
    void handlePostCommented(CommentCreatedEventDto dto);
    void handlePostCommentDeleted(CommentDeletedEventDto dto);
    void handlePostLiked(PostLikedEventDto dto);
    void handlePostUnliked(PostUnlikedEventDto dto);
}
