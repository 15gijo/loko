package com.team15gijo.chat.domain.model.v2;

import com.team15gijo.common.model.base.BaseEntity;
import jakarta.persistence.Entity;
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
@Table(name = "p_chat_room_participants")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE p_chat_room_participants SET deleted_at = now(), deleted_by = updated_by WHERE id = ?")
public class ChatRoomParticipantV2 extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long userId;

    private Boolean activation; // 활성화 상태(True: 참여, False: 퇴장)

    public void nonActivate() {
        activation = false;
    }
}
