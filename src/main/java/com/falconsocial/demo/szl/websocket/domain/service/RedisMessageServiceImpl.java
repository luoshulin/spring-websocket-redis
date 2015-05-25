package com.falconsocial.demo.szl.websocket.domain.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.falconsocial.demo.szl.websocket.domain.model.Message;

/**
 * {@link MessageService} implementation which uses Redis for persisting {@link Message}.
 * 
 * @author szabol
 *
 */
@Service
@Profile("!embedded")
public class RedisMessageServiceImpl implements MessageService {

    private static final String MESSAGE_KEY_FORMAT = "%s:%s";
    private static final String ALL_MESSAGE_KEY = "messages";

    @Autowired
    private RedisTemplate<String, Message> messageRedisTemplate;

    @Override
    public Message create(Message message) {

        message = checkNotNull(message, "Message is empty!");

        // Add by id
        messageRedisTemplate.opsForValue().set(generateKey(message.getId()), message);
        // Add to messages collection
        messageRedisTemplate.opsForList().rightPush(ALL_MESSAGE_KEY, message);

        return message;
    }

    @Override
    public Collection<Message> findAll() {
        Collection<Message> contacts = messageRedisTemplate.opsForList().range(ALL_MESSAGE_KEY, 0, Integer.MAX_VALUE);
        return contacts;
    }

    @Override
    public Message findById(String id) {
        String key = generateKey(id);
        Message found = messageRedisTemplate.opsForValue().get(key);

        return found;
    }

    @Override
    public void delete(String id) {

        Message deleted = findAndCheck(id);

        messageRedisTemplate.opsForValue().set(generateKey(deleted.getId()), null);
        messageRedisTemplate.opsForList().remove(ALL_MESSAGE_KEY, 1, deleted);
    }

    private Message findAndCheck(String id) {
        Message findById = findById(id);
        checkState(null != findById, "No message was found with id:[%s]", id);

        return findById;
    }

    private String generateKey(String id) {
        return String.format(MESSAGE_KEY_FORMAT, Message.class.getSimpleName(), checkNotNull(id, "Id must be set!"));
    }
}
