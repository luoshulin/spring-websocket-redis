package com.falconsocial.demo.szl.websocket.model;

import java.time.LocalDateTime;

/**
 * Message representing messages pushed towards websocket clients
 * 
 * @author szabol
 *
 */
public class PushMessage extends Message {

    private LocalDateTime date;

    private String sender;

    public PushMessage(String content, MessageType type, String sender) {
        super(content, type);
        this.sender = sender;
        date = LocalDateTime.now();
    }

    public PushMessage(Message origin, String sender) {
        this(origin.getContent(), origin.getType(), sender);
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getSender() {
        return sender;
    }

}
