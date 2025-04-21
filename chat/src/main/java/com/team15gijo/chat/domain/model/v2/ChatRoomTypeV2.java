package com.team15gijo.chat.domain.model.v2;

public enum ChatRoomTypeV2 {
    INDIVIDUAL("INDIVIDUAL"),
    ORGANIZATION("ORGANIZATION");

    private final String type;

    ChatRoomTypeV2(String type) {
        this.type = type;
    }
}
