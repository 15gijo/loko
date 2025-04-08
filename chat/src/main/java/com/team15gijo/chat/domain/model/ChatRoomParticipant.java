package com.team15gijo.chat.domain.model;

import jakarta.persistence.Entity;
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
@Table(name = "p_chat_room_participants")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID chatRoomId;

    private Long userId;

    private Boolean activation; // 활성화 상태(True: 참여, False: 퇴장)
}
