package com.falconsocial.demo.szl.websocket.domain.model;

import java.time.LocalDateTime;

import com.falconsocial.demo.szl.websocket.web.model.BasicMessage;

/**
 * Domain object modeling messages in Redis
 * 
 * @author szabol
 *
 */
public class Message extends BasicMessage {

    private Long id;

    private LocalDateTime date;

    private String sender;

    public Message() {

    }

    public Message(String content, MessageType type, String sender) {
        super(content, type);
        this.sender = sender;
        date = LocalDateTime.now();
    }

    public Message(BasicMessage origin, String sender) {
        this(origin.getContent(), origin.getType(), sender);
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getSender() {
        return sender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
