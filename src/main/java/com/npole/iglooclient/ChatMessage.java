package com.npole.igloo;

import java.io.*;

public class ChatMessage implements Serializable {
    protected static final long serialVersionUUID = 1112122200L;
    static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2, ESKIMO = 3;
    private int type;
    private String message;

    ChatMessage (int type, String message) {
        this.type = type;
        this.message = message;
    }

    int getType() {
        return type;
    }

    String getMessage() {
        return message;
    }
}
