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
        this.sender = sender;
        date = LocalDateTime.now();
        setContent(content);
        setType(type);
    }

    public PushMessage(Message origin, String sender) {
        this(origin.getContent(), origin.getType(), sender);
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

}
