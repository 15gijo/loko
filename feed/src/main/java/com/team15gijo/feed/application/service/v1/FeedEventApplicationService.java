package com.team15gijo.feed.application.service.v1;

import com.team15gijo.feed.domain.model.Feed;
import com.team15gijo.feed.domain.repository.FeedRepository;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.CommentCreatedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostCreatedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostDeletedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostUpdatedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostViewedEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedEventApplicationService implements FeedEventService {

    private final FeedRepository feedRepository;

    @Transactional
    public void handlePostCreated(PostCreatedEventDto dto) {
        log.info("✅ POST_CREATED received: {}", dto.toString());
        Feed feed = dto.toEntity();
        feedRepository.save(feed);
        log.info("Feed 정보 저장 완료 - postId: {}", feed.getPostId());
    }

    @Transactional
    public void handlePostUpdated(PostUpdatedEventDto dto) {
        log.info("✅ POST_UPDATED received: {}", dto.toString());
        // TODO: Redis 저장 / DB 업데이트 / 인덱싱 등 처리
        Feed feed = feedRepository.findById(dto.getPostId()).orElse(null);
        if (feed == null) {
            log.warn("Feed not found for postId: {}", dto.getPostId());
            return;
        }
        feed.updateFeed(dto);
        feedRepository.save(feed);
        log.info("Feed 정보 수정 완료 - postId: {}", feed.getPostId());
    }

    @Transactional
    public void handlePostDeleted(PostDeletedEventDto dto) {
        log.info("🗑️ POST_DELETED received: {}", dto.toString());
        // TODO: DB 업데이트 / Redis 제거 / 상태 동기화
        Feed feed = feedRepository.findById(dto.getPostId()).orElse(null);
        if (feed == null) {
            log.warn("Feed not found for postId: {}", dto.getPostId());
            return;
        }
        feedRepository.delete(feed);
    }

    @Transactional
    public void handlePostViewed(PostViewedEventDto dto) {
        log.info("👀 POST_VIEWED received: {}", dto.toString());
        // TODO: 조회수 누적 처리 등
        Feed feed = feedRepository.findById(dto.getPostId()).orElse(null);
        if (feed == null) {
            log.warn("Feed not found for postId: {}", dto.getPostId());
            return;
        }
        feed.updateFeedViews(dto.getViews());
        feedRepository.save(feed);
        log.info("Feed 정보(조회수) 수정 완료 - postId: {}", feed.getPostId());
    }

    @Transactional
    public void handlePostCommented(CommentCreatedEventDto dto) {
        log.info("💬 POST_COMMENTED received: {}", dto.toString());
        // TODO: 댓글 수 증가 처리
    }
}
