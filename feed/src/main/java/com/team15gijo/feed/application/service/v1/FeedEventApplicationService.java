package com.team15gijo.feed.application.service.v1;

import com.team15gijo.feed.domain.model.Feed;
import com.team15gijo.feed.domain.repository.FeedRepository;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.CommentCreatedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.CommentDeletedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostCreatedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostDeletedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostLikedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostUnlikedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostUpdatedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostViewedEventDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FeedEventApplicationService implements FeedEventService {

    private static final int VIEW_WEIGHT = 1;
    private static final int COMMENT_WEIGHT = 2;
    private static final double POST_WEIGHT = 2.5;

    private final FeedRepository feedRepository;

    public void handlePostCreated(PostCreatedEventDto dto) {
        log.info("✅ POST_CREATED received: {}", dto.toString());
        Feed feed = dto.toEntity();
        feedRepository.save(feed);
        log.info("Feed 정보 저장 완료 - postId: {}", feed.getPostId());
    }

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
        updatePopularityScore(dto.getPostId()); //인기 점수 갱신
        log.info("Feed 정보(조회수) 수정 완료 - postId: {}", feed.getPostId());
    }

    public void handlePostCommented(CommentCreatedEventDto dto) {
        log.info("💬 POST_COMMENTED received: {}", dto.toString());
        // TODO: 댓글 수 증가 처리
        Feed feed = feedRepository.findById(dto.getPostId()).orElse(null);
        if (feed == null) {
            log.warn("Feed not found for postId: {}", dto.getPostId());
            return;
        }
        feed.updateFeedCommentCount(dto.getCommentCount());
        feedRepository.save(feed);
        updatePopularityScore(dto.getPostId()); //인기 점수 갱신
        log.info("Feed 댓글 수 수정 완료 - postId: {}", feed.getPostId());
    }

    public void handlePostCommentDeleted(CommentDeletedEventDto dto) {
        log.info("💬 POST_COMMENTED received: {}", dto.toString());
        // TODO: 댓글 수 감소 처리
        Feed feed = feedRepository.findById(dto.getPostId()).orElse(null);
        if (feed == null) {
            log.warn("Feed not found for postId: {}", dto.getPostId());
            return;
        }
        feed.updateFeedCommentCount(dto.getCommentCount());
        feedRepository.save(feed);
        updatePopularityScore(dto.getPostId()); //인기 점수 갱신
        log.info("Feed 댓글 수 수정 완료 - postId: {}", feed.getPostId());
    }

    public void handlePostLiked(PostLikedEventDto dto) {
        log.info( "❤️ POST_LIKED received: {}", dto.toString());
        // TODO: 좋아요 수 증가 처리
        Feed feed = feedRepository.findById(dto.getPostId()).orElse(null);
        if (feed == null) {
            log.warn("Feed not found for postId: {}", dto.getPostId());
            return;
        }
        feed.updateFeedLikeCount(dto.getLikeCount());
        feedRepository.save(feed);
        updatePopularityScore(dto.getPostId()); //인기 점수 갱신
        log.info("Feed 좋아요 수 수정 완료 - postId: {}", feed.getPostId());
    }

    public void handlePostUnliked(PostUnlikedEventDto dto) {
        log.info( "💔 POST_UNLIKED received: {}", dto.toString());
        // TODO: 좋아요 수 감소 처리
        Feed feed = feedRepository.findById(dto.getPostId()).orElse(null);
        if (feed == null) {
            log.warn("Feed not found for postId: {}", dto.getPostId());
            return;
        }
        feed.updateFeedLikeCount(dto.getLikeCount());
        feedRepository.save(feed);
        updatePopularityScore(dto.getPostId()); //인기 점수 갱신
        log.info("Feed 좋아요 수 수정 완료 - postId: {}", feed.getPostId());
    }

    /**
     * 인기 점수 갱신
     */
    public void updatePopularityScore(UUID postId) {
        Feed feed = feedRepository.findById(postId).orElse(null);
        if (feed == null) {
            log.warn("Feed not found for postId: {}", postId);
            return;
        }
        // 인기 점수 : (조회수 * 1) + (좋아요 수 * 2) + (댓글 수 * 2.5)
        double score = feed.getViews() * VIEW_WEIGHT
                + feed.getLikeCount() * COMMENT_WEIGHT
                + feed.getCommentCount() * POST_WEIGHT;
        // DB 정보 업데이트
        feed.updateFeedPopularityScore(score);
    }
}
