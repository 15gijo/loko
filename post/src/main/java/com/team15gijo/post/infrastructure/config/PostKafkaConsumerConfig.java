package com.team15gijo.post.infrastructure.config;

import com.team15gijo.post.infrastructure.kafka.dto.v1.CommentCountEventDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableRetry
public class PostKafkaConsumerConfig {

    /**
     * comment-count-events 토픽 소비용 컨테이너 팩토리
     *  - 총 3회 재시도(1초 간격)
     *  - 재시도 모두 실패 시 "comment-count-events.DLT" 로 메시지 전송
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CommentCountEventDto>
    commentCountKafkaListenerContainerFactory(
            ConsumerFactory<String, CommentCountEventDto> cf,
            KafkaTemplate<String, CommentCountEventDto> dlqTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, CommentCountEventDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                dlqTemplate,
                (ConsumerRecord<?,?> cr, Exception e) ->
                        new org.apache.kafka.common.TopicPartition(cr.topic() + ".DLT", cr.partition())
        );

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1_000L, 2));
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}
