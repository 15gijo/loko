package com.team15gijo.chat.domain.model;

public enum ChatRoomType {
    INDIVIDUAL("INDIVIDUAL"),
    ORGANIZATION("ORGANIZATION");

    private final String type;

    ChatRoomType(String type) {
        this.type = type;
    }
}
