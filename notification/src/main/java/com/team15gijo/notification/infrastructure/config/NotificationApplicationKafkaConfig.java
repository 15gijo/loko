package com.team15gijo.notification.infrastructure.config;

import com.team15gijo.notification.application.dto.v1.message.ChatNotificationEventDto;
import com.team15gijo.notification.application.dto.v1.message.CommentNotificationEventDto;
import com.team15gijo.notification.application.dto.v1.message.FollowNotificationEventDto;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@EnableKafka
@Configuration
public class NotificationApplicationKafkaConfig {

    /**
     *  Kafka Producer Config
     *
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

//    @Bean
//    public KafkaTemplate<String, String> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
//
//    @Bean
//    public ProducerFactory<String, String> producerFactory() {
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        return new DefaultKafkaProducerFactory<>(configProps);
//    }

    // 댓글 이벤트용 KafkaTemplate(테스트 용으로 나눈 코드, 각자 서버에서는 위의 코드를 참고)
    @Bean
    public ProducerFactory<String, CommentNotificationEventDto> commentProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, CommentNotificationEventDto> commentKafkaTemplate() {
        return new KafkaTemplate<>(commentProducerFactory());
    }

    // 팔로우 이벤트용 KafkaTemplate(테스트 용으로 나눈 코드, 각자 서버에서는 위의 코드를 참고)
    @Bean
    public ProducerFactory<String, FollowNotificationEventDto> followProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, FollowNotificationEventDto> followKafkaTemplate() {
        return new KafkaTemplate<>(followProducerFactory());
    }

    // 채팅 이벤트용 KafkaTemplate(테스트 용으로 나눈 코드, 각자 서버에서는 위의 코드를 참고)
    @Bean
    public ProducerFactory<String, ChatNotificationEventDto> chatProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, ChatNotificationEventDto> chatKafkaTemplate() {
        return new KafkaTemplate<>(chatProducerFactory());
    }


    /**
     *  Kafka Consumer Config
     *
     */
//    @Bean
//    public ConsumerFactory<String, String> consumerFactory() {
//        Map<String, Object> configProps = new HashMap<>();                                     // 컨슈머 팩토리 설정을 위한 맵을 생성.
//        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);              // Kafka 브로커의 주소를 설정.
//        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-service");
//        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);     // 메시지 키의 디시리얼라이저 클래스를 설정.
//        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);      // 메시지 값의 디시리얼라이저 클래스를 설정.
//        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");                   // 역직렬화할 때 data 신뢰성 모두 허용
//        return new DefaultKafkaConsumerFactory<>(configProps);                                     // 설정된 프로퍼티로 DefaultKafkaConsumerFactory를 생성하여 반환.
//    }
//
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();  // Factory를 생성.
//        factory.setConsumerFactory(consumerFactory());                  // 컨슈머 팩토리를 리스너 컨테이너 팩토리에 설정.
//        return factory;
//    }

    @Bean
    public ConsumerFactory<String, CommentNotificationEventDto> commentConsumerFactory() {
        JsonDeserializer<CommentNotificationEventDto> deserializer = new JsonDeserializer<>(CommentNotificationEventDto.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG, "notification-service",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
                ),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CommentNotificationEventDto> commentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CommentNotificationEventDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(commentConsumerFactory());
        return factory;
    }

    // Follow
    @Bean
    public ConsumerFactory<String, FollowNotificationEventDto> followConsumerFactory() {
        JsonDeserializer<FollowNotificationEventDto> deserializer = new JsonDeserializer<>(FollowNotificationEventDto.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG, "notification-service",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
                ),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FollowNotificationEventDto> followKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FollowNotificationEventDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(followConsumerFactory());
        return factory;
    }

    // Chat
    @Bean
    public ConsumerFactory<String, ChatNotificationEventDto> chatConsumerFactory() {
        JsonDeserializer<ChatNotificationEventDto> deserializer = new JsonDeserializer<>(ChatNotificationEventDto.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG, "notification-service",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
                ),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatNotificationEventDto> chatKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ChatNotificationEventDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(chatConsumerFactory());
        return factory;
    }
}
