package com.team15gijo.post.infrastructure.config;

import com.team15gijo.post.infrastructure.kafka.dto.v1.CommentCountEventDto;
import com.team15gijo.post.infrastructure.kafka.dto.v1.FeedEventDto;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class ProducerApplicationKafkaConfig {
    @Bean
    public ProducerFactory<String, FeedEventDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // 하드코딩 아니고 주입받아야한다.
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, FeedEventDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // 2) CommentCountEventDto 용 KafkaTemplate
    @Bean
    public ProducerFactory<String, CommentCountEventDto> commentCountProducerFactory() {
        Map<String,Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,    "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,  StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }
    @Bean
    public KafkaTemplate<String, CommentCountEventDto> commentCountKafkaTemplate() {
        return new KafkaTemplate<>(commentCountProducerFactory());
    }
}
