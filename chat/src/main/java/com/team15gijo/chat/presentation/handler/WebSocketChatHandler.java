//package com.team15gijo.chat.presentation.handler;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.team15gijo.chat.domain.model.ConnectionType;
//import com.team15gijo.chat.presentation.dto.v1.ChatMessageRequestDto;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//import java.util.UUID;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
///**
// * socket 통신은 서버와 클라이언트가 1:N 관계
// * 한 서버와 여러 클라이언트가 접속 가능
// * 이 서버에 여러 클라이언트가 발송한 메시지를 받아 웹소켓 통신을 처리해줄 Handler 설정 필요
// * 클라이언트로 받은 메시지 log 출력 및 클라이언트한테 환영 메시지 전송
// * */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class WebSocketChatHandler extends TextWebSocketHandler {
//
//    private final ObjectMapper objectMapper;
//
//    // 현재 연결된 웹소켓 세션들
//    // 세션 추가 및 종료 반영 - afterConnectionEstablished(), afterConnectionClose()
//    private final Set<WebSocketSession> sessions = new HashSet<>();
//
//    // 채팅방 당 연결된 세션
//    // chatRoomId: {session1, session2}
//    // 채팅 메시지 보낼 채팅방을 찾고 해당 채팅방에 속한 세션들한테 메시지 전송
//    private final Map<UUID, Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();
//
//    // 소켓 연결 확인
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        log.info("{} established", session.getId());
//        sessions.add(session);
//    }
//
//    /**
//     * 웹소켓 클라이언트로부터 채팅 메시지를 전달받아 채팅 메시지 객체로 변환
//     * 전달받은 메시지에 담긴 채팅방 고유ID로 발송 대상 채팅방 정보를 조회
//     * 해당 채팅방에 입장해있는 모든 클라이언트들에게 타입에 따른 메시지 발송
//     * */
//    // 소켓 통신 시 메시지(TextMessage)의 전송을 다루는 부분
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        String payload = message.getPayload();
//        log.info("payload: {}", payload);
//
//        // 페이로드 -> chatMessageDto로 변환
//        ChatMessageRequestDto chatMessageResponse = objectMapper.readValue(payload, ChatMessageRequestDto.class);
//        log.info("chatMessageDto: {}", chatMessageResponse.toString());
//
//        UUID chatRoomId = chatMessageResponse.getChatRoomId();
//        log.info("chatRoomId: {}", chatRoomId);
//        // 메모리 상에 채팅방에 대한 세션 없으면 만들기
//        if(!chatRoomSessionMap.containsKey(chatRoomId)) {
//            chatRoomSessionMap.put(chatRoomId, new HashSet<>());
//        }
//        Set<WebSocketSession> chatRoomSession = chatRoomSessionMap.get(chatRoomId);
//
//        // message에 담긴 타입을 확인
//        // ChatDto의 열거형인 MessageType 안에 있는 ENTER 과 동일한 값이라면
//        if(chatMessageResponse.getConnectionType() == ConnectionType.ENTER) {
//            // sessions 에 넘어온 session을 담고
//            chatRoomSession.add(session);
//        }
//        if(chatRoomSession.size() == 1) {
//            removeClosedSession(chatRoomSession);
//        }
//        sendMessageToChatRoom(chatMessageResponse, chatRoomSession);
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        log.info("{} closed", session.getId());
//        sessions.remove(session);
//    }
//
//    // 채팅 관련 메소드
//    private void removeClosedSession(Set<WebSocketSession> chatRoomSession) {
//        chatRoomSession.removeIf(session -> !sessions.contains(session));
//    }
//
//    private void sendMessageToChatRoom(ChatMessageRequestDto chatMessageResponse, Set<WebSocketSession> chatRoomSession) {
//        chatRoomSession.parallelStream().forEach(session -> sendMessage(session, chatMessageResponse));
//    }
//
//    public <T> void sendMessage(WebSocketSession session, T message) {
//        try {
//            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//        }
//    }
//
//    // 채팅방 생성 시 초기 메시지 전송
//    public void sendInitialMessage(UUID chatRoomId, ChatMessageRequestDto message) {
//        Set<WebSocketSession> chatRoomSession = chatRoomSessionMap.get(chatRoomId);
//        if (chatRoomSession != null) {
//            sendMessageToChatRoom(message, chatRoomSession);
//        }
//    }
//}
