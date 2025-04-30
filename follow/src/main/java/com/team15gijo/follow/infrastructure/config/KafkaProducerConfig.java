package com.team15gijo.follow.infrastructure.config;

import com.team15gijo.follow.infrastructure.kafka.dto.v2.FollowEventDto;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, FollowEventDto> internalProducerFactory() {
        return new DefaultKafkaProducerFactory<>(internalProducerConfigs());
    }

    @Bean
    public KafkaTemplate<String, FollowEventDto> internalKafkaTemplate() {
        return new KafkaTemplate<>(internalProducerFactory());
    }

    private Map<String, Object> internalProducerConfigs() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 5);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        return configProps;
    }

    @Bean
    public ProducerFactory<String, String> externalProducerFactory() {
        return new DefaultKafkaProducerFactory<>(externalProducerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> externalKafkaTemplate() {
        return new KafkaTemplate<>(externalProducerFactory());
    }

    @Bean
    public Map<String, Object> externalProducerConfigs() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); //모든 replica ack
        configProps.put(ProducerConfig.RETRIES_CONFIG, 5); //5회 재시도
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 5); //5ms 대기 후 batch 전송 (전송률 증가)
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); //중복 제거
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
                5); //요청 5개 묶어서 전송(idempotence 순서 깨짐 방지)

        return configProps;

    }

}
