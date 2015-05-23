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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

}
