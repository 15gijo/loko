package com.team15gijo.chat.domain.model;

import com.team15gijo.chat.application.dto.v1.ChatRoomResponseDto;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_chat_rooms")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ChatRoomType chatRoomType;

    public ChatRoomResponseDto toResponse() {
        return ChatRoomResponseDto.builder()
            .id(id)
            .chatRoomType(chatRoomType)
            .build();
    }
}
