package com.team15gijo.chat.infrastructure.kafka.service;

import com.mongodb.MongoQueryException;
import com.mongodb.MongoSecurityException;
import com.team15gijo.chat.domain.model.v2.ChatMessageDocumentV2;
import com.team15gijo.chat.domain.model.v2.ConnectionTypeV2;
import com.team15gijo.chat.domain.repository.v2.ChatMessageRepositoryV2;
import com.team15gijo.chat.infrastructure.kafka.dto.ChatMessageEventDto;
import com.team15gijo.chat.infrastructure.kafka.dto.ChatNotificationEventDto;
import com.team15gijo.chat.infrastructure.slack.service.SlackNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageKafkaConsumerService {

    private final ChatMessageRepositoryV2 chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationKafkaProducerService notificationKafkaProducerService;

    private final SlackNotificationService slackNotificationService;

    private static final String MESSAGE_DLT_TOPIC = "chat_message_event-dlt";

    // TODO: topic을 chatRoomId로 분리할까?
    // 토픽 - 채팅 메시지 내용 구독
    // 재시도 가능한 오류(Retry) & 재시도 불가능한 오류 구분(DLT)
    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, maxDelay = 3000, random = true), // 1~3초 랜덤 재시도
        dltStrategy = DltStrategy.FAIL_ON_ERROR, // 재시도 실패 후 DLQ 이동
        dltTopicSuffix = "-dlt", // DLQ 토픽 접미사
        exclude = { // 즉시 DLQ 전송 예외 클래스 리스트
            MongoQueryException.class, // 쿼리 실행 중 오류
            MongoSecurityException.class, // 인증 또는 권한 부여 실패 예외
            NullPointerException.class // 데이터 문제 예외(connectionType==null)
        }
    )
    @KafkaListener(topics = "chat_message_event", groupId = "chat-service")
    @Transactional
    public void consumeChatMessageEvent(ChatMessageEventDto chatMessageEventDto) {
        log.info("[MessageKafkaConsumerService] consumeChatMessageEvent 시작");

        try {
            // 1. 메시지 변환
            ChatMessageDocumentV2 savedMessage = ChatMessageEventDto.from(chatMessageEventDto);
//            // TODO: 예외 발생 시나리오 -> 임시 코드(테스트 이후 삭제 예정)
//            if(savedMessage.getMessageContent().contains("retry")) {
//                throw new RuntimeException("런타임 아웃 오류 발생");
//            } else if(savedMessage.getMessageContent().contains("dlt")) {
//                throw new NullPointerException("데이터 null 값으로 인한 오류 발생");
//            }
            // 2. mongoDB 메시지 저장
            chatMessageRepository.save(savedMessage);
            log.info("[mongoDB 저장] - messageContent: {}", savedMessage.getMessageContent());

            // 3. WebSocket 연결된 메시지 처리
            if(savedMessage.getConnectionType() == ConnectionTypeV2.ENTER) {
                // 3-1. 입장 메시지
                log.info("[webSocket '/topic/v2/chat/enter/{}' 연결] kafka 컨슈머가 '입장 메시지' 처리",
                    savedMessage.getChatRoomId());
                messagingTemplate.convertAndSend("/topic/v2/chat/enter/" + savedMessage.getChatRoomId(), savedMessage);
                log.info("[MessageKafkaConsumerService] 입장 메시지 처리 종료");
            } else if(savedMessage.getConnectionType() == ConnectionTypeV2.CHAT) {
                // 3-2. 채팅 메시지
                log.info("[webSocket '/topic/v2/chat/{}' 연결] kafka 컨슈머가 '채팅 메시지' 처리",
                    chatMessageEventDto.getChatRoomId());
                messagingTemplate.convertAndSend("/topic/v2/chat/" + chatMessageEventDto.getChatRoomId(), savedMessage);
                log.info("[MessageKafkaConsumerService] 채팅 메시지 처리 완료");

                log.info("[MessageKafkaConsumerService] \uD83D\uDCCC 보내는 사람 senderId:{}, senderNickname:{}", savedMessage.getSenderId(), savedMessage.getSenderNickname());
                log.info("[MessageKafkaConsumerService] \uD83D\uDCCC 받는 사람 receiverId:{}, receiverNickname:{}",savedMessage.getReceiverId(), savedMessage.getReceiverNickname());

                // 4. 메시지 전송 알림 처리 - 보내는 사람의 닉네임 전달
                notificationKafkaProducerService.sendChatCreate(ChatNotificationEventDto
                    .from(chatMessageEventDto.getReceiverId(), chatMessageEventDto.getSenderNickname(), chatMessageEventDto.getMessageContent()));
                log.info("[MessageKafkaConsumerService] 채팅 메시지 전송 알림 이후 종료");
            }
        } catch (Exception e) {
            log.error("카프카 컨슈머 메시지 처리 에러 발생: {}", e.getMessage());
            throw e; // 오류 예외 던져서 재시도 및 DLQ로 전송
        }
    }

    // DLQ 처리를 위한 카프카 리스너
    @KafkaListener(topics = MESSAGE_DLT_TOPIC, groupId = "chat-service")
    public void handleChatMessageEventDlt(ChatMessageEventDto chatMessageEventDto) {
        log.info("[MessageKafkaConsumerService] handleChatMessageEventDlt 메서드 실행");

        // kafka에 저장된 데이터를 슬랙 알림 연동으로 에러 발생 시, 모니터링 및 수동으로 오류 해결
        slackNotificationService.handleDltMessage(chatMessageEventDto, MESSAGE_DLT_TOPIC);
    }

    // 토픽 - 채팅방 접속 에러 메시지 구독
    @KafkaListener(topics = "chat_error_event", groupId = "chat-service")
    @Transactional
    public void consumeChatErrorEvent(ChatMessageEventDto chatMessageEventDto) {
        log.info("[MessageKafkaConsumerService] consumeChatErrorEvent 시작");
        ChatMessageDocumentV2 errorDocument = ChatMessageEventDto.from(chatMessageEventDto);

        log.info("[webSocket '/topic/v2/chat/errors/{}' 연결] kafka 컨슈머가 메시지 처리",
            chatMessageEventDto.getSenderId());
        messagingTemplate.convertAndSend("/topic/v2/chat/errors/" + chatMessageEventDto.getSenderId(), errorDocument.getMessageContent());
        log.info("[MessageKafkaConsumerService] consumeChatErrorEvent 종료");
    }

}
