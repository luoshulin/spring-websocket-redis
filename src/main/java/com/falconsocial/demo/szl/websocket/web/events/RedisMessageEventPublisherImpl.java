package com.falconsocial.demo.szl.websocket.web.events;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.falconsocial.demo.szl.websocket.domain.model.Message;
import com.falconsocial.demo.szl.websocket.domain.redis.NewMessageEventListener;

/**
 * {@link MessageEventPublisher} implementation which using Redis for event publishing
 * 
 * @author szabol
 *
 */
@Profile("!embedded")
@Component
public class RedisMessageEventPublisherImpl implements MessageEventPublisher {

    @Autowired
    private RedisTemplate<String, Message> messageRedisTemplate;

    @Override
    public void publishMessageReceived(Message message) {
        // Publish on Redis
        messageRedisTemplate.convertAndSend(NewMessageEventListener.EVENT_RECEIVE_MESSAGE_KEY, checkNotNull(message, "The received message must not be null!"));
    }

    protected void setMessageRedisTemplate(RedisTemplate<String, Message> messageRedisTemplate) {
        this.messageRedisTemplate = messageRedisTemplate;
    }

}
