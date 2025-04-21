package com.team15gijo.chat.domain.model.v1;

public enum ConnectionType {
    ENTER("ENTER"),
    CHAT("CHAT"),
    EXIT("EXIT");

    private final String type;

    ConnectionType(String type) {
        this.type = type;
    }
}
