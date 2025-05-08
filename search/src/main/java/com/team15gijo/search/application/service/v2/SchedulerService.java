package com.team15gijo.search.application.service.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team15gijo.search.domain.model.PostUpdateDlq;
import com.team15gijo.search.domain.repository.PostElasticsearchRepository;
import com.team15gijo.search.domain.repository.PostUpdateDlqRepository;
import com.team15gijo.search.infrastructure.kafka.dto.v2.CommentCreatedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.CommentDeletedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostCreatedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostDeletedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostLikedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostUnlikedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostUpdatedEventDto;
import com.team15gijo.search.infrastructure.kafka.dto.v2.PostViewedEventDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final ObjectMapper objectMapper;
    private final PostService postService;
    private final PostUpdateDlqRepository dlqRepository;

    @Scheduled(fixedDelay = 60000) // 1분마다 실행
    @Transactional
    public void retryDlqMessages() {
        log.info("DLT 데이터 게시글 업데이트 스케줄러 동작");
        List<PostUpdateDlq> failedMessages = dlqRepository.findByResolvedFalse();
        log.info("DLT 데이터 Size : {}", failedMessages.size());

        for (PostUpdateDlq dlq : failedMessages) {
            try {
                switch (dlq.getType()) {
                    case "POST_CREATED" -> {
                        PostCreatedEventDto dto = objectMapper.readValue(dlq.getPayload(), PostCreatedEventDto.class);
                        postService.handlePostCreated(dto);
                    }
                    case "POST_UPDATED" -> {
                        PostUpdatedEventDto dto = objectMapper.readValue(dlq.getPayload(), PostUpdatedEventDto.class);
                        postService.handlePostUpdated(dto);
                    }
                    case "POST_DELETED" -> {
                        PostDeletedEventDto dto = objectMapper.readValue(dlq.getPayload(), PostDeletedEventDto.class);
                        postService.handlePostDeleted(dto);
                    }
                    case "POST_VIEWED" -> {
                        PostViewedEventDto dto = objectMapper.readValue(dlq.getPayload(), PostViewedEventDto.class);
                        postService.handlePostViewed(dto);
                    }
                    case "COMMENT_CREATED" -> {
                        CommentCreatedEventDto dto = objectMapper.readValue(dlq.getPayload(), CommentCreatedEventDto.class);
                        postService.handlePostCommented(dto);
                    }
                    case "COMMENT_DELETED" -> {
                        CommentDeletedEventDto dto = objectMapper.readValue(dlq.getPayload(), CommentDeletedEventDto.class);
                        postService.handlePostCommentDeleted(dto);
                    }
                    case "POST_LIKED" -> {
                        PostLikedEventDto dto = objectMapper.readValue(dlq.getPayload(), PostLikedEventDto.class);
                        postService.handlePostLiked(dto);
                    }
                    case "POST_UNLIKED" -> {
                        PostUnlikedEventDto dto = objectMapper.readValue(dlq.getPayload(), PostUnlikedEventDto.class);
                        postService.handlePostUnliked(dto);
                    }
                    default -> {
                        log.warn("❓ 알 수 없는 DLT 타입: {}", dlq.getType());
                        continue;
                    }
                }
                dlq.setResolved(true);
                dlqRepository.save(dlq);
            } catch (Exception e) {
                log.info("스케줄러 처리 중 알림 저장 실패 id={}, type={}, reason={}", dlq.getId(), dlq.getType(), e.getMessage(), e);
            }
        }
    }

}
