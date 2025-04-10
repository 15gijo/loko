package com.team15gijo.feed.infrastructure.kafka.controller.v1;

import com.team15gijo.feed.infrastructure.kafka.dto.v1.FeedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostCreatedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostDeletedEventDto;
import com.team15gijo.feed.infrastructure.kafka.dto.v1.PostUpdatedEventDto;
import com.team15gijo.feed.infrastructure.kafka.service.v1.ProducerTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/kafka/post/test")
@RequiredArgsConstructor
public class ProducerTestController {

    private final ProducerTestService producerService;

    /**
     * 게시글 생성/수정/삭제
     * @param topic
     * @param key
     * @param message
     * @return
     */
    @GetMapping("/send/feed-events")
    public String sendMessage(@RequestParam(value = "topic", required = true, defaultValue = "feed_events") String topic,
                              @RequestParam(value = "key", required = false) String key,
                              @RequestParam(value = "message", required = false, defaultValue = "테스트용 글입니다.") String message,
                              @RequestParam(value = "eventType", required = false, defaultValue = "post_created") String eventType,
                              @RequestParam(value = "postId", required = false) UUID postId) {
        if (postId == null) postId = UUID.randomUUID();
        FeedEventDto feedEventDto = createEventMessageByType(eventType, message, postId);
        producerService.sendMessage(topic, key, feedEventDto);
        return "Message sent to Kafka topic";
    }

    private PostCreatedEventDto getPostCreatedEventDto(String message, UUID postId) {
        return PostCreatedEventDto.builder()
                .postId(UUID.randomUUID())
                .postContent(message)
                .build();
    }

    private PostUpdatedEventDto getPostUpdatedEventDto(String message, UUID postId) {
        return PostUpdatedEventDto.builder()
                .postId(postId)
                .postContent(message)
                .build();
    }

    private PostDeletedEventDto getPostDeletedEventDto(UUID postId) {
        return PostDeletedEventDto.builder()
                .postId(postId)
                .build();
    }

    private FeedEventDto createEventMessageByType(String eventType, String message, UUID postId) {
        if (eventType == null || message == null) return null;

        return switch (eventType) {
            case "post_created" -> getPostCreatedEventDto(message, postId);
            case "post_updated" -> getPostUpdatedEventDto(message, postId);
            case "post_deleted" -> getPostDeletedEventDto(postId);
            default -> null;
        };
    }

}