package com.team15gijo.chat.infrastructure.kafka.service;

import com.team15gijo.chat.domain.model.v2.ChatMessageDocumentV2;
import com.team15gijo.chat.domain.model.v2.ConnectionTypeV2;
import com.team15gijo.chat.domain.repository.v2.ChatMessageRepositoryV2;
import com.team15gijo.chat.infrastructure.client.FeignClientService;
import com.team15gijo.chat.infrastructure.client.v2.ai.dto.MessageFilteringResponseDto;
import com.team15gijo.chat.infrastructure.kafka.dto.ChatMessageEventDto;
import com.team15gijo.chat.infrastructure.kafka.dto.ChatNotificationEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    private final FeignClientService feignClientService;
    private final MongoTemplate mongoTemplate;

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
                log.info("[MessageKafkaConsumerService] 채팅 메시지 처리 완료");

                log.info("[MessageKafkaConsumerService] \uD83D\uDCCC 보내는 사람 senderId:{}, senderNickname:{}", savedMessage.getSenderId(), savedMessage.getSenderNickname());
                log.info("[MessageKafkaConsumerService] \uD83D\uDCCC 받는 사람 receiverId:{}, receiverNickname:{}",savedMessage.getReceiverId(), savedMessage.getReceiverNickname());

                // 4. 메시지 전송 알림 처리 - 보내는 사람의 닉네임 전달
                notificationKafkaProducerService.sendChatCreate(ChatNotificationEventDto
                    .from(chatMessageEventDto.getReceiverId(), chatMessageEventDto.getSenderNickname(), chatMessageEventDto.getMessageContent()));
                log.info("[MessageKafkaConsumerService] 채팅 메시지 전송 알림 처리");

                // 5. AI 서비스의 feign client 호출로 메시지 유해성 판단 및 시스템 삭제 처리
                log.info("[MessageKafkaConsumerService] ai API로 유해 메시지 필터링 전 - id:{}", savedMessage.get_id());
                MessageFilteringResponseDto isHarmfulResponse = feignClientService.fetchIsHarmfulByMessage(savedMessage.getMessageContent());
                log.info("[MessageKafkaConsumerService] ai API로 유해 메시지 필터링 후 - isHarmfulResponse:{}", isHarmfulResponse);

                // 유해한 메시지로 필터링 적용 = true
                if(isHarmfulResponse.getIsHarmful()) {
                    log.info("[MessageKafkaConsumerService] 유해성 판단 결과={}", isHarmfulResponse.getIsHarmful());

                    // mongoDB에서 채팅방의 메시지 소프트 삭제 처리
                    deleteChatMessageForChatRoomId(savedMessage.get_id(), isHarmfulResponse.getDeleteBy());

                    // TODO: 삭제 후, 채팅방에서 실시간 숨김 처리
                }
            }
        } catch (Exception e) {
            log.error("카프카 컨슈머 메시지 처리 에러 발생: {}", e.getMessage());
            // 오류 처리 로직 (예: Dead Letter Queue로 전송, 재시도 등)
        }
    }

    // mongoDB에서 채팅방의 메시지 소프트 삭제
    private void deleteChatMessageForChatRoomId(
        String id, Long systemId) {
        log.info("[MessageKafkaConsumerService] deleteChatMessageForChatRoomId - 메시지({}) 소프트 삭제 전", id);
        // 채팅방의 단일 메시지 조회
        Query query = Query.query(
            Criteria.where("_id").is(id)
                .and("deletedAt").is(null));

        ChatMessageDocumentV2 filteringMessage = mongoTemplate.findOne(query, ChatMessageDocumentV2.class);
        log.info("[MessageKafkaConsumerService] filteringMessage={}", filteringMessage);

        if (filteringMessage == null) {
            log.error("채팅방에 해당하는 메시지 내역이 존재하지 않습니다.");
        } else {
            // 메시지 소프트 삭제
            filteringMessage.softDelete(systemId);
            chatMessageRepository.save(filteringMessage);
            log.info("[MessageKafkaConsumerService] deleteChatMessageForChatRoomId - 메시지({}) 소프트 삭제 완료", id);
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
