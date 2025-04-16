package com.team15gijo.chat.domain.model;

public enum ConnectionType {
    ENTER("ENTER"),
    CHAT("CHAT"),
    EXIT("EXIT");

    private final String type;

    ConnectionType(String type) {
        this.type = type;
    }
}
