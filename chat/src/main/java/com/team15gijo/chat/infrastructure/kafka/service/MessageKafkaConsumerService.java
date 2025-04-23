package com.team15gijo.chat.infrastructure.kafka.service;

import com.team15gijo.chat.domain.model.v2.ChatMessageDocumentV2;
import com.team15gijo.chat.domain.model.v2.ConnectionTypeV2;
import com.team15gijo.chat.domain.repository.v2.ChatMessageRepositoryV2;
import com.team15gijo.chat.infrastructure.kafka.dto.ChatMessageEventDto;
import com.team15gijo.chat.infrastructure.kafka.dto.ChatNotificationEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageKafkaConsumerService {

    private final ChatMessageRepositoryV2 chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationKafkaProducerService notificationKafkaProducerService;

    // TODO: topic을 chatRoomId로 분리할까?
    // 토픽 - 채팅 메시지 내용 구독
    @KafkaListener(topics = "chat_message_event", groupId = "chat-service")
    @Transactional
    public void consumeChatMessageEvent(ChatMessageEventDto chatMessageEventDto) {
        log.info("[MessageKafkaConsumerService] consumeChatMessageEvent 시작");

        try {
            // 1. 메시지 변환
            ChatMessageDocumentV2 savedMessage = ChatMessageEventDto.from(chatMessageEventDto);
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
                log.info("[MessageKafkaConsumerService] 채팅 메시지 처리");
                // 4. 메시지 전송 알림 처리
                // 수정이 필요한 사항 : 현재 보내는 사람의 닉네임을 받을 수 없어 임시로 받는 사람의 닉네임을 보냄. 추후 수정 필요
                notificationKafkaProducerService.sendChatCreate(ChatNotificationEventDto
                    .from(chatMessageEventDto.getReceiverId(), chatMessageEventDto.getReceiverNickname(), chatMessageEventDto.getMessageContent()));
                log.info("[MessageKafkaConsumerService] 채팅 메시지 전송 알림 이후 종료");
            }

        } catch (Exception e) {
            log.error("카프카 컨슈머 메시지 처리 에러 발생: {}", e.getMessage());
            // 오류 처리 로직 (예: Dead Letter Queue로 전송, 재시도 등)
        }
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
