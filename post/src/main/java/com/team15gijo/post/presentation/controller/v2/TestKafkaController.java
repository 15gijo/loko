
package com.team15gijo.post.presentation.controller.v2;

import com.team15gijo.post.infrastructure.kafka.dto.v1.CommentCountEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/test/kafka")
@RequiredArgsConstructor
public class TestKafkaController {
    private final KafkaTemplate<String, CommentCountEventDto> kafkaTemplate;

    /**
     * 테스트용: 입력한 JSON을 comment-count-events 토픽으로 보냅니다.
     */
    @PostMapping("/comment-count")
    public ResponseEntity<Void> sendCommentCountEvent(@RequestBody CommentCountEventDto dto) {
        kafkaTemplate.send("comment-count-events", dto);
        return ResponseEntity.ok().build();
    }
}
