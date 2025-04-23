package com.team15gijo.user.infrastructure.config;

import com.team15gijo.user.infrastructure.kafka.dto.UserElasticsearchRequestDto;
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
public class UserApplicationKafkaConfig {

    @Bean
    public ProducerFactory<String, UserElasticsearchRequestDto> searchProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // 재시도 및 에러 처리 설정 추가
//        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
//        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, UserElasticsearchRequestDto> searchKafkaTemplate() {
        return new KafkaTemplate<>(searchProducerFactory());
    }
}
