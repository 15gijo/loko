package com.team15gijo.chat.domain.model.v2;

public enum ChatMessageTypeV2 {
    TEXT("TEXT"),
    IMAGE("IMAGE"),
    VIDEO("VIDEO"),
    FILE("FILE");

    private final String type;

    ChatMessageTypeV2(String type) {
        this.type = type;
    }
}
