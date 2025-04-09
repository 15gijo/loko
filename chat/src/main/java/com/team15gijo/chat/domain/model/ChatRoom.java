package com.team15gijo.chat.domain.model;

import com.team15gijo.chat.application.dto.v1.ChatRoomResponseDto;
import com.team15gijo.common.base.BaseEntity;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_chat_rooms")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE p_chat_rooms SET deleted_at = now(), deleted_by = updated_by WHERE chat_room_id = ?")
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID chatRoomId;

    @Enumerated(EnumType.STRING)
    private ChatRoomType chatRoomType;

    public ChatRoomResponseDto toResponse() {
        return ChatRoomResponseDto.builder()
            .chatRoomId(chatRoomId)
            .chatRoomType(chatRoomType)
            .build();
    }
}
