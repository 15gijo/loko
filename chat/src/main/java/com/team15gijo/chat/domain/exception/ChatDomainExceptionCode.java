package com.team15gijo.chat.domain.exception;

import com.team15gijo.common.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatDomainExceptionCode implements ExceptionCode {
    USER_NICK_NAME_NOT_EXIST(HttpStatus.NOT_FOUND, "유저의 닉네임이 존재하지 않습니다."),
    CHAT_ROOM_INDIVIDUAL_NUMBER_LIMIT(HttpStatus.BAD_REQUEST, "1:1 채팅은 2명만 참여할 수 있습니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다."),
    CHAT_ROOM_USER_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방 유저 아이디를 찾을 수 없습니다."),
    MESSAGE_NOT_FOUND_FOR_CHAT_ROOM(HttpStatus.NOT_FOUND, "채팅방에 해당하는 메시지 내역이 찾을 수 없습니다."),
    MESSAGE_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅 메시지가 존재하지 않습니다."),

    SENT_AT_DATETIME_PARSE(HttpStatus.BAD_REQUEST, "sentAt 형식이 유효하지 않습니다. 'yyyy-MM-dd' 형식이어야 합니다."),
    KAFKA_DLT_PROCESS_ERROR(HttpStatus.BAD_REQUEST, "DLT 메시지 처리 중 오류가 발생했습니다."),
    SEND_NOTIFICATION_TO_SLACK_FOR_JSON(HttpStatus.BAD_REQUEST, "슬랙 알림 전송 JSO 파싱 오류가 발생했습니다."),
    SEND_NOTIFICATION_TO_SLACK_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "슬랙 알림 전송 예외가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
