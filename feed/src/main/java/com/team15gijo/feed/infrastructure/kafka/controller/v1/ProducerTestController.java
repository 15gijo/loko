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
                              @RequestParam(value = "eventType", required = false, defaultValue = "post_created") String eventType) {
        FeedEventDto feedEventDto = createEventMessageByType(eventType, message);
        producerService.sendMessage(topic, key, feedEventDto);
        return "Message sent to Kafka topic";
    }

    private PostCreatedEventDto getPostCreatedEventDto(String message) {
        return PostCreatedEventDto.builder()
                .postContent(message)
                .build();
    }

    private PostUpdatedEventDto getPostUpdatedEventDto(String message) {
        return PostUpdatedEventDto.builder()
                .postContent(message)
                .build();
    }

    private PostDeletedEventDto getPostDeletedEventDto() {
        return PostDeletedEventDto.builder()
                .postId(UUID.randomUUID())
                .build();
    }

    private FeedEventDto createEventMessageByType(String eventType, String message) {
        if (eventType == null || message == null) return null;

        return switch (eventType) {
            case "post_created" -> getPostCreatedEventDto(message);
            case "post_updated" -> getPostUpdatedEventDto(message);
            case "post_deleted" -> getPostDeletedEventDto();
            default -> null;
        };
    }

}