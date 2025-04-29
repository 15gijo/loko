package com.team15gijo.search.application.service.v2;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.search.domain.exception.SearchDomainExceptionCode;
import com.team15gijo.search.domain.model.PostDocument;
import com.team15gijo.search.domain.repository.PostElasticsearchRepository;
import com.team15gijo.search.infrastructure.kafka.dto.v2.CommentCreatedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.CommentDeletedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostCreatedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostDeletedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostLikedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostUnlikedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostUpdatedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostViewedEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostElasticsearchRepository postElasticsearchRepository;

    @Override
    public void handlePostCreated(PostCreatedEventDto dto) {
        log.info("✅ POST_CREATED received: {}", dto.toString());
        try {
            PostDocument post = dto.toEntity();
            postElasticsearchRepository.save(post);
            log.info("게시글의 정보 저장 완료 - postId: {}", post.getPostId());
        } catch (Exception e) {
            log.error("ElasticSearch 게시글 저장 실패", e);
            throw new CustomException(SearchDomainExceptionCode.POST_SAVE_FAIL);
        }
    }

    @Override
    public void handlePostUpdated(PostUpdatedEventDto dto) {
        log.info("✅ POST_UPDATED received: {}", dto.toString());
        // TODO: Redis 저장 / DB 업데이트 / 인덱싱 등 처리
        PostDocument post = postElasticsearchRepository.findById(dto.getPostId()).orElse(null);
        if (post == null) {
            log.warn("post not found for postId: {}", dto.getPostId());
            throw new CustomException(SearchDomainExceptionCode.POST_NOT_FOUND);
        }
        try {
            post.updateFeed(dto);
            postElasticsearchRepository.save(post);
            log.info("게시글의 정보 수정 완료 - postId: {}", post.getPostId());
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생 - postId: {}, error: {}", post.getPostId(), e.getMessage(), e);
            throw new CustomException(SearchDomainExceptionCode.POST_UPDATE_FAIL);
        }
    }

    @Override
    public void handlePostDeleted(PostDeletedEventDto dto) {
        log.info("🗑️ POST_DELETED received: {}", dto.toString());
        // TODO: DB 업데이트 / Redis 제거 / 상태 동기화
        PostDocument post  = postElasticsearchRepository.findById(dto.getPostId()).orElse(null);
        if (post == null) {
            log.warn("post not found for postId: {}", dto.getPostId());
            throw new CustomException(SearchDomainExceptionCode.POST_NOT_FOUND);
        }
        try {
            postElasticsearchRepository.delete(post);
            log.info("게시글 삭제 완료 - postId: {}", post.getPostId());
        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생 - postId: {}, error: {}", post.getPostId(), e.getMessage(), e);
            throw new CustomException(SearchDomainExceptionCode.POST_DELETE_FAIL);
        }

    }

    @Override
    public void handlePostViewed(PostViewedEventDto dto) {
        log.info("👀 POST_VIEWED received: {}", dto.toString());
        // TODO: 조회수 누적 처리 등
        PostDocument post  = postElasticsearchRepository.findById(dto.getPostId()).orElse(null);
        if (post == null) {
            log.warn("post not found for postId: {}", dto.getPostId());
            throw new CustomException(SearchDomainExceptionCode.POST_NOT_FOUND);
        }
        try {
            post.updateViews(dto.getViews());
            postElasticsearchRepository.save(post);
            log.info("게시글의 조회수 수정 완료 - postId: {}", post.getPostId());
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생 - postId: {}, error: {}", post.getPostId(), e.getMessage(), e);
            throw new CustomException(SearchDomainExceptionCode.VIEW_UPDATE_FAIL);
        }
    }

    @Override
    public void handlePostCommented(CommentCreatedEventDto dto) {
        log.info("💬 POST_COMMENTED received: {}", dto.toString());
        // TODO: 댓글 수 증가 처리
        PostDocument post  = postElasticsearchRepository.findById(dto.getPostId()).orElse(null);
        if (post == null) {
            log.warn("post not found for postId: {}", dto.getPostId());
            throw new CustomException(SearchDomainExceptionCode.POST_NOT_FOUND);
        }
        try {
            post.updateCommentCount(dto.getCommentCount());
            postElasticsearchRepository.save(post);
            log.info("게시글의 댓글 수 증가 완료 - postId: {}", post.getPostId());
        } catch (Exception e) {
            log.error("게시글 댓글 수 증가 실패 - postId: {}, error: {}", post.getPostId(), e.getMessage(), e);
            throw new CustomException(SearchDomainExceptionCode.COMMENT_COUNT_UP_FAIL);
        }
    }

    @Override
    public void handlePostCommentDeleted(CommentDeletedEventDto dto) {
        log.info("💬 POST_COMMENTED received: {}", dto.toString());
        // TODO: 댓글 수 감소 처리
        PostDocument post  = postElasticsearchRepository.findById(dto.getPostId()).orElse(null);
        if (post == null) {
            log.warn("post not found for postId: {}", dto.getPostId());
            throw new CustomException(SearchDomainExceptionCode.POST_NOT_FOUND);
        }
        try {
            post.updateCommentCount(dto.getCommentCount());
            postElasticsearchRepository.save(post);
            log.info("게시글의 댓글 수 감소 완료 - postId: {}", post.getPostId());
        } catch (Exception e) {
            log.error("게시글 댓글 수 감소 실패 - postId: {}, error: {}", post.getPostId(), e.getMessage(), e);
            throw new CustomException(SearchDomainExceptionCode.COMMENT_COUNT_DOWN_FAIL);
        }
    }

    @Override
    public void handlePostLiked(PostLikedEventDto dto) {
        log.info( "❤️ POST_LIKED received: {}", dto.toString());
        // TODO: 좋아요 수 증가 처리
        PostDocument post  = postElasticsearchRepository.findById(dto.getPostId()).orElse(null);
        if (post == null) {
            log.warn("post not found for postId: {}", dto.getPostId());
            throw new CustomException(SearchDomainExceptionCode.POST_NOT_FOUND);
        }
        try {
            post.updateLikeCount(dto.getLikeCount());
            postElasticsearchRepository.save(post);
            log.info("게시글의 좋아요 수 증가 완료 - postId: {}", post.getPostId());
        } catch (Exception e) {
            log.error("게시글 좋아요 수 증가 실패 - postId: {}, error: {}", post.getPostId(), e.getMessage(), e);
            throw new CustomException(SearchDomainExceptionCode.LIKE_COUNT_UP_FAIL);
        }
    }

    @Override
    public void handlePostUnliked(PostUnlikedEventDto dto) {
        log.info( "💔 POST_UNLIKED received: {}", dto.toString());
        // TODO: 좋아요 수 감소 처리
        PostDocument post  = postElasticsearchRepository.findById(dto.getPostId()).orElse(null);
        if (post == null) {
            log.warn("post not found for postId: {}", dto.getPostId());
            throw new CustomException(SearchDomainExceptionCode.POST_NOT_FOUND);
        }
        try {
            post.updateLikeCount(dto.getLikeCount());
            postElasticsearchRepository.save(post);
            log.info("게시글의 좋아요 수 감소 완료 - postId: {}", post.getPostId());
        } catch (Exception e) {
            log.error("게시글 좋아요 수 감소 실패 - postId: {}, error: {}", post.getPostId(), e.getMessage(), e);
            throw new CustomException(SearchDomainExceptionCode.LIKE_COUNT_DOWN_FAIL);
        }
    }
}
