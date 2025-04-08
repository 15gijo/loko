package com.team15gijo.chat.domain.model;

import com.team15gijo.chat.presentation.dto.v1.ChatRoomParticipantResponseDto;
import com.team15gijo.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_chat_room_participants")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE p_chat_room_participants SET deleted_at = now(), deleted_by = updated_by WHERE id = ?")
public class ChatRoomParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    private Long userId;

    private Boolean activation; // 활성화 상태(True: 참여, False: 퇴장)

    public ChatRoomParticipantResponseDto toResponse() {
        return ChatRoomParticipantResponseDto.builder()
            .chatRoomId(chatRoom.getChatRoomId())
            .userId(userId)
            .activation(activation)
            .build();
    }

    public void nonActivate() {
        activation = false;
    }
}
