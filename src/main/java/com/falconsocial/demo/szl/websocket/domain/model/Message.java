package com.falconsocial.demo.szl.websocket.domain.model;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;

import java.time.LocalDateTime;
import java.util.UUID;

import com.falconsocial.demo.szl.websocket.web.model.BasicMessage;

/**
 * Immutable domain object modeling messages in persistent store
 * 
 * @author szabol
 *
 */
public class Message extends BasicMessage {

    private String id = UUID.randomUUID().toString();

    private LocalDateTime date = LocalDateTime.now();

    private String sender;

    private Message() {

    }

    private Message(String content, MessageType type, String sender) {
        super(content, type);
        this.sender = sender;
        date = LocalDateTime.now();
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getSender() {
        return sender;
    }

    public String getId() {
        return id;
    }

    /**
     * Builder class for constructing Immutable {@link Message} objects
     * 
     * @author szabol
     *
     */
    public static class MessageBuilder {

        private Message message;

        private MessageBuilder(Message message) {
            this.message = message;
        }

        public MessageBuilder withContent(String content) {
            message.setContent(content);
            return this;
        }

        public MessageBuilder withType(MessageType type) {
            message.setType(type);
            return this;
        }

        public MessageBuilder sentBy(String sender) {
            message.sender = sender;
            return this;
        }

        public static MessageBuilder empty() {
            return new MessageBuilder(new Message());
        }

        public static MessageBuilder copyOf(Message message) {
            Message newMessage = new Message();
            newMessage.setContent(message.getContent());
            newMessage.setType(message.getType());
            newMessage.date = message.getDate();
            newMessage.sender = message.getSender();
            newMessage.id = message.getId();

            return new MessageBuilder(newMessage);
        }

        public static MessageBuilder randomInfo() {
            Message newMessage = new Message();
            newMessage.setContent(randomAlphabetic(200));
            newMessage.setType(MessageType.INFO);
            newMessage.sender = randomAlphabetic(20);

            return new MessageBuilder(newMessage);
        }

        public Message build() {
            return message;
        }

    }

}
