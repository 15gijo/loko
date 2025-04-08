package com.team15gijo.notification;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@EnableKafka
@Configuration
public class NotificationApplicationKafkaConfig {

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();                                     // 컨슈머 팩토리 설정을 위한 맵을 생성.
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");              // Kafka 브로커의 주소를 설정.
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);     // 메시지 키의 디시리얼라이저 클래스를 설정.
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);      // 메시지 값의 디시리얼라이저 클래스를 설정.
        return new DefaultKafkaConsumerFactory<>(configProps);                                     // 설정된 프로퍼티로 DefaultKafkaConsumerFactory를 생성하여 반환.
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();  // Factory를 생성.
        factory.setConsumerFactory(consumerFactory());                  // 컨슈머 팩토리를 리스너 컨테이너 팩토리에 설정.
        return factory;
    }
}
