package com.team15gijo.post.application.listener;

import com.team15gijo.post.infrastructure.kafka.dto.v1.CommentCountEventDto;
import com.team15gijo.post.application.service.v2.PostServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCountListener {

    private final PostServiceV2 postService;

    @KafkaListener(
            topics = "comment-count-events",
            containerFactory = "commentCountKafkaListenerContainerFactory",
            groupId = "post-service-group"
    )
    public void onCommentCount(CommentCountEventDto event) {
//        // 테스트용 강제 실패
//        if (event.getDelta() == 9999) {
//            throw new RuntimeException("DLQ 테스트용 예외 발생");
//        }
        // Retry + DLQ 로직이 적용된 컨테이너 팩토리를 통해 호출됩니다.
        if (event.getDelta() > 0) {
            postService.addCommentCount(event.getPostId());
        } else if (event.getDelta() < 0) {
            postService.minusCommentCount(event.getPostId());
        }
    }
}
