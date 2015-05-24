package com.falconsocial.demo.szl.websocket.model;

/**
 * POJO which models messages on the REST layer
 * 
 * @author szabol
 *
 */
public class Message {

    private String content;

    private MessageType type;

    public Message() {
    }

    public Message(String content, MessageType type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public MessageType getType() {
        return type;
    }

}
