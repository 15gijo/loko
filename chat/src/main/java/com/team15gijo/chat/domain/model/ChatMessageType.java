package com.team15gijo.chat.domain.model;

public enum ChatMessageType {
    TEXT("TEXT"),
    IMAGE("IMAGE"),
    VIDEO("VIDEO"),
    FILE("FILE");

    private final String type;

    ChatMessageType(String type) {
        this.type = type;
    }
}
