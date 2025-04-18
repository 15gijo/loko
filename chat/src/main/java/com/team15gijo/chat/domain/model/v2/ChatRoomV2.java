package com.team15gijo.chat.domain.model.v2;

import com.team15gijo.chat.application.dto.v2.ChatRoomResponseDtoV2;
import com.team15gijo.common.model.base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
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
public class ChatRoomV2 extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID chatRoomId;

    @Enumerated(EnumType.STRING)
    private ChatRoomTypeV2 chatRoomType;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id") // ChatRoomParticipants 테이블의 chat_room_id 외래키
    private Set<ChatRoomParticipantV2> chatRoomParticipant;

    public ChatRoomResponseDtoV2 toResponse() {
        return ChatRoomResponseDtoV2.builder()
                .chatRoomId(chatRoomId)
                .chatRoomType(chatRoomType)
                .chatRoomParticipants(chatRoomParticipant)
                .build();
    }
}
