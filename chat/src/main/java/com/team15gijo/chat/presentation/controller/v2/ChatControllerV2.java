package com.team15gijo.chat.presentation.controller.v2;

import static com.team15gijo.chat.domain.exception.ChatDomainExceptionCode.SENT_AT_DATETIME_PARSE;

import com.team15gijo.chat.application.dto.v2.ChatMessageResponseDtoV2;
import com.team15gijo.chat.application.dto.v2.ChatRoomResponseDtoV2;
import com.team15gijo.chat.application.service.impl.v2.ChatServiceImplV2;
import com.team15gijo.chat.domain.model.v2.ChatMessageDocumentV2;
import com.team15gijo.chat.domain.model.v2.ChatRoomV2;
import com.team15gijo.chat.presentation.dto.v2.ChatMessageRequestDtoV2;
import com.team15gijo.chat.presentation.dto.v2.ChatRoomRequestDtoV2;
import com.team15gijo.common.annotation.RoleGuard;
import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.common.exception.CustomException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller("chatControllerV2")
@RequiredArgsConstructor
@RequestMapping("/api/v2/chats")
public class ChatControllerV2 {
    private final ChatServiceImplV2 chatService;

    /**
     * 채팅방 생성(chatRoomType, receiver)에 따른 채팅방 참여자 생성
     * 채팅방이 INDIVIDUAL 타입에서는 3명 이상 채팅방 생성 불가
     * X-User-Id : 채팅방 생성 시 사용
     */
    @RoleGuard(min = "USER")
    @PostMapping("/rooms")
    @ResponseBody
    public ResponseEntity<ApiResponse<ChatRoomResponseDtoV2>> createChatRoom(
        @RequestBody ChatRoomRequestDtoV2 requestDto,
        @RequestHeader("X-User-Id") Long userId
    ) {
        ChatRoomResponseDtoV2 response = chatService.createChatRoom(requestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("채팅방 생성되었습니다.", response));
    }

    /**
     * 채팅방 단일 조회
     * X-User-Id : chatRoomId 에 참여하는 사용자 검증
     */
    @RoleGuard(min = "USER")
    @GetMapping("/rooms/{chatRoomId}")
    public ResponseEntity<ApiResponse<ChatRoomResponseDtoV2>> getChatRoom(
        @PathVariable("chatRoomId") UUID chatRoomId,
        @RequestHeader("X-User-Id") Long userId
    ) {
        ChatRoomResponseDtoV2 response = chatService.getChatRoom(chatRoomId, userId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success("채팅방 조회되었습니다.", response));
    }

    /**
     * 채팅방 전체 조회
     * X-User-Id : 채팅방 전체에서 참여하는 사용자 필터링
     */
    @RoleGuard(min = "USER")
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<Page<ChatRoomV2>>> getChatRooms(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "chatRoomId") String sortField,
        @RequestParam(defaultValue = "desc") String sortDirection,
        @RequestHeader("X-User-Id") Long userId
    ) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<ChatRoomV2> chatRooms = chatService.getChatRooms(pageable, userId);
        log.info("chatRooms = {}, size = {}", chatRooms.toString(), chatRooms.getSize());
        return ResponseEntity.ok(ApiResponse.success("채팅방 목록이 조회되었습니다.", chatRooms));
    }

    /**
     * 채팅방 퇴장(비활성화) -> 삭제(Batch 또는 비동기 처리)
     * -> 1:1 채팅방에서 1명의 사용자 퇴장(채팅방 참여자 비활성화로 변경)
     * -> 채팅방의 모든 참여자 퇴장 시 채팅방/채팅방 참여자/채팅 메시지 소프트 삭제 처리
     * 응답 result = false : 참여자만 비활성화
     * 응답 result = true : 참여자 모두 비활성화로 채팅방/참여자 소프트삭제 처리됨
     * X-User-Id : chatRoomId 에 참여하는 사용자 비활성화로 변경
     */
    @RoleGuard(min = "USER")
    @PatchMapping("/rooms/{chatRoomId}")
    public ResponseEntity<ApiResponse<Boolean>> exitChatRoom(
        @PathVariable("chatRoomId") UUID chatRoomId,
        @RequestHeader("X-User-Id") Long userId) {

        boolean result = chatService.exitChatRoom(chatRoomId, userId);
        String message = "";
        if(result) {
            message = chatRoomId + " 채팅방 및 참여자 모두가 삭제되었습니다.";
        } else {
            message = userId + "의 " + chatRoomId + " 채팅방이 비활성화 되었습니다.";
        }
        return ResponseEntity.ok(ApiResponse.success(message, result));
    }

    /**
     * 수신자 닉네임 검증 및 웹소켓 연결 시, 발송자 닉네임 전달
     */
    @GetMapping("/validate/nickname/{receiverNickname}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateNickname(
        @PathVariable("receiverNickname") String receiverNickname) {
        Map<String, Object> response = chatService.validateNickname(receiverNickname);
        return ResponseEntity.ok(ApiResponse.success("닉네임 유효성 검증 완료되었습니다.", response));
    }

    /**
     * 소켓 연결 시 사용되는 엔드포인트
     * 채팅방 ID 유효성 검증 API
     */
    @GetMapping("/validate/{chatRoomId}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> validateChatRoomId(
        @PathVariable("chatRoomId") UUID chatRoomId
    ) {
        Map<String, Boolean> response = chatService.validateChatRoomId(chatRoomId);
        return ResponseEntity.ok(ApiResponse.success("chatRoomId 유효성 검증 성공하였습니다.", response));
    }

    /**
     * 소켓 연결 시 사용되는 엔드포인트
     * 채팅방 ID에 해당하는 senderId 유효성 검증 API
     */
    @GetMapping("/validate/{chatRoomId}/{senderId}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> validateSenderId(
        @PathVariable("chatRoomId") UUID chatRoomId,
        @PathVariable("senderId") Long senderId
    ) {
        Map<String, Boolean> response = chatService.validateSenderId(chatRoomId, senderId);
        return ResponseEntity.ok(ApiResponse.success("senderId 유효성 검증 성공하였습니다.", response));
    }

    /**
     * 소켓 연결 중단 시, redis 삭제 API 호출
     * Redis key(chatRoomId:senderId)-value(SessionID, senderId, chatRoomId)로 변경
     */
    @GetMapping("/redis/delete/{chatRoomId}/{senderId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteRedisSenderId(
        @PathVariable("chatRoomId") UUID chatRoomId,
        @PathVariable("senderId") String senderId
    ) {
        log.info("[ChatControllerV2] deleteRedisSenderId 메소드 시작");
        Boolean response = chatService.deleteRedisSenderId(chatRoomId, senderId);
        log.info("[ChatControllerV2] response = {}", response);
        return ResponseEntity.ok(ApiResponse.success("Redis 캐시 삭제 성공하였습니다.", response));
    }

    /**
     * mongoDB 에서 메시지 페이징 조회(이전 메시지 불러오기) API
     * Page 전체 조회(sentAt ASC 정렬)
     * index 에서 fetch 호출 시, forEach 메서드 에러 발생으로 ApiResponse 객체 사용X
     */
    @GetMapping("message/page/{chatRoomId}/{senderId}")
    public ResponseEntity<Page<ChatMessageDocumentV2>> getMessagesByChatRoomId(
        @PathVariable("chatRoomId") UUID chatRoomId,
        @PathVariable("senderId") Long senderId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "30") int size,
        @RequestParam(defaultValue = "sentAt") String sortField,
        @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<ChatMessageDocumentV2> chatMessages = chatService.getMessagesByChatRoomId(chatRoomId, senderId, pageable);
        return ResponseEntity.ok(chatMessages);
    }

    /**
     * 채팅방 메시지 상세 조회
     * chatRoomId에 속하는 참여자는 메시지 고유 ID로 메시지 상세 조회 모두 가능
     * X-User-Id : chatRoomId 에 참여하는 사용자 검증
     */
    @RoleGuard(min = "USER")
    @GetMapping("message/{chatRoomId}/{id}")
    public ResponseEntity<ApiResponse<ChatMessageResponseDtoV2>> getMessageById(
        @PathVariable("chatRoomId") UUID chatRoomId,
        @PathVariable("id") String id,
        @RequestHeader("X-User-Id") Long userId
    ) {
        ChatMessageResponseDtoV2 responseDto = chatService.getMessageById(chatRoomId, id, userId);
        return ResponseEntity.ok(ApiResponse.success("메시지 조회 성공했습니다.", responseDto));
    }

    /**
     * "/ws-stomp" 경로로 소켓 연결 시, 쿼리파라미터 senderId를 추출하여 Redis 캐시 저장
     * Redis key(chatRoomId:senderId)-value(SessionID, senderId, chatRoomId)로 변경
     * @param headerAccessor : sessionID 추출
     */
    @MessageMapping("/v2/chat/connect/{chatRoomId}/{senderId}")
    public ResponseEntity<ApiResponse<String>> connectChatRoom(
        @DestinationVariable UUID chatRoomId,
        @DestinationVariable Long senderId,
        SimpMessageHeaderAccessor headerAccessor) {
        log.info("[ChatControllerV2] connectChatRoom 메소드 시작");
        log.info("헤더 headerAccessor: {}", headerAccessor.getSessionAttributes());
        chatService.connectChatRoom(chatRoomId, senderId, headerAccessor);
        return ResponseEntity.ok(ApiResponse.success("웹소켓 연결 처리하였습니다."));
    }

    /**
     * "/ws-stomp" 경로로 소켓 연결하여
     * "/topic"을 구독하는 서버에서 실시간 메시지 송수신 가능
     * "/app" 시작하는 경로 stomp 메시지 전송하면 @MessageMapping 으로 연결
     */
    @MessageMapping("/v2/chat/{chatRoomId}")
    public ResponseEntity<ApiResponse<String>> sendMessage(
        @RequestBody ChatMessageRequestDtoV2 requestDto,
        SimpMessageHeaderAccessor headerAccessor
    ) {
        log.info("[ChatControllerV2] sendMessage 메소드 시작");
        log.info("헤더 headerAccessor: {} ", headerAccessor.getSessionAttributes());
        log.info("채팅 message: {}", requestDto.toString());

        try {
            chatService.sendMessage(requestDto, headerAccessor);
            return ResponseEntity.ok(ApiResponse.success("카프카로 채팅 메시지 발행 처리하였습니다."));
        } catch (Exception e) {
            headerAccessor.setMessageTypeIfNotSet(SimpMessageType.MESSAGE);
            headerAccessor.setLeaveMutable(true);
            headerAccessor.setHeader("error", e.getMessage()); // 오류 메시지 헤더에 추가
            throw e;
        }
    }

    /**
     * 메시지 queryDSl 적용(날짜별/메시지 검색)
     *  정규 표현식의 모든 메타 문자를 자동으로 이스케이프 처리
     *  "^" :  URL 인코딩 시 403 Bad Request 발생으로 -> "%5E" 입력하면 "^"로 검색 처리
     *  "+" : URL 인코딩 시 " "(공백 문자) 전달되어 메시지 전체 조회됨 -> "%2B" 입력하면 "+"로 검색 처리
     */
    @RoleGuard(min = "USER")
    @GetMapping("/message/search")
    public  ResponseEntity<ApiResponse<Page<ChatMessageDocumentV2>>> searchMessages(
        @RequestParam UUID chatRoomId,
        @RequestParam(required = false) String sentAt,
        @RequestParam(required = false) String messageContent,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "30") int size,
        @RequestParam(defaultValue = "sentAt") String sortField,
        @RequestParam(defaultValue = "asc") String sortDirection,
        @RequestHeader("X-User-Id") Long userId
    ) {
        log.info("[ChatControllerV2] searchMessages 메소드 시작");
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        log.info(">> direction={}", direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        log.info(">> sentAt: {}", sentAt);
        LocalDateTime parsedSentAt = null;
        if(sentAt != null && !sentAt.isEmpty()) {
            // 기본적으로 서버에서 JVM의 로컬 타임존(KST)
            // MongoDB에는 UTC 기준으로 조건 불일치로 sentAt 파싱 시, ZoneOffset.UTC로 고정
            try {
                // yyyy-MM-dd 형식만 유효하도록 검증
                parsedSentAt = LocalDate.parse(sentAt).atStartOfDay(ZoneOffset.UTC).toLocalDateTime();
                log.info(">> parsedSentAt: {}", parsedSentAt);
            } catch (DateTimeParseException e) {
                log.warn(">> sentAt 형식이 유효하지 않습니다. 'yyyy-MM-dd' 형식이어야 합니다. 입력값: {}", sentAt);
                throw new CustomException(SENT_AT_DATETIME_PARSE);
            }
        }
        log.info(">> messageContent: {}", messageContent);
        Page<ChatMessageDocumentV2> messages = chatService.searchMessages(chatRoomId, parsedSentAt, messageContent, userId, pageable);

        log.info("[ChatControllerV2] searchMessages 메소드 종료");
        return ResponseEntity.ok(ApiResponse.success("채팅 메시지 내역이 검색되었습니다.", messages));
    }
}
