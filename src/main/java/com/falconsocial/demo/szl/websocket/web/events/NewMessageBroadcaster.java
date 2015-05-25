package com.falconsocial.demo.szl.websocket.web.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.falconsocial.demo.szl.websocket.domain.model.Message;

/**
 * Redis message listener which subscribes on "newMessages" channel and broadcasts the received {@link Message} object on Websocket.
 * 
 * @author szabol
 *
 */
@Component
public class NewMessageBroadcaster {

    public static final String WEBSOCKET_MESSAGE_TOPIC_PATH = "/topic/messages";

    public static final String EVENT_RECEIVE_MESSAGE_KEY = "newMessages";

    private static final Logger logger = LoggerFactory.getLogger(NewMessageBroadcaster.class);

    @Autowired
    private SimpMessagingTemplate brokerMessagingTemplate;

    public void handleMessage(Message message, String channel) {

        logger.debug("Message receive event fired on channel:[{}]", channel);

        // Push on websocket
        brokerMessagingTemplate.convertAndSend(WEBSOCKET_MESSAGE_TOPIC_PATH, message);
    }

    public void setBrokerMessagingTemplate(SimpMessagingTemplate brokerMessagingTemplate) {
        this.brokerMessagingTemplate = brokerMessagingTemplate;
    }

}
