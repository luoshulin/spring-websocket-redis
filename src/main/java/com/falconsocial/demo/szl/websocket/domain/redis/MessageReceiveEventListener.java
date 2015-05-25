package com.falconsocial.demo.szl.websocket.domain.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.falconsocial.demo.szl.websocket.domain.model.Message;
import com.falconsocial.demo.szl.websocket.domain.service.MessageService;

/**
 * Redis message listener POJO which subscribes on "newMessages" topic. On a received messages it saves the message through {@link MessageService}
 * 
 * @author szabol
 *
 */
@Component
public class MessageReceiveEventListener {

    public static final String EVENT_RECEIVE_MESSAGE_KEY = "newMessages";

    private static final Logger logger = LoggerFactory.getLogger(MessageReceiveEventListener.class);

    @Autowired
    private MessageService messageService;

    public void handleMessage(Message message, String channel) {

        logger.debug("Message receive event fired on channel:[{}]", channel);

        messageService.create(message);
    }

    protected void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

}
