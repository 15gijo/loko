package com.team15gijo.chat.domain.model.v2;

public enum ConnectionTypeV2 {
    ENTER("ENTER"),
    CHAT("CHAT"),
    EXIT("EXIT");

    private final String type;

    ConnectionTypeV2(String type) {
        this.type = type;
    }
}
