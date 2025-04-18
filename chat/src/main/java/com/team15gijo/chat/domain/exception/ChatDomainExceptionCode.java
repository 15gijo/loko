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
    MESSAGE_ID_NOT_FOUND(HttpStatus.NOT_FOUND,"채팅 메시지가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
