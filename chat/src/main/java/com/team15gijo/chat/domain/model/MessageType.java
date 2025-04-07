package com.team15gijo.chat.domain.model;

public enum MessageType {
    TEXT("TEXT"),
    IMAGE("IMAGE"),
    VIDEO("VIDEO"),
    FILE("FILE");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }
}
