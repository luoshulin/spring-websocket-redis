package com.falconsocial.demo.szl.websocket.domain.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
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

    private static final String MESSAGE_KEY_PREFIX = "message";
    private static final String ALL_MESSAGE_KEY_PREFIX = "messages";

    @Autowired
    private RedisTemplate<String, Message> messageRedisTemplate;

    @Autowired
    private RedisAtomicLong messageRedisKeySequence;

    @Override
    public Message create(Message message) {

        message = checkNotNull(message, "Message is empty!");
        message.setId(messageRedisKeySequence.incrementAndGet());

        // Add by id
        messageRedisTemplate.opsForValue().set(generateKey(message.getId()), message);
        // Add to messages collection
        messageRedisTemplate.opsForSet().add(ALL_MESSAGE_KEY_PREFIX, message);

        return message;
    }

    @Override
    public Collection<Message> findAll() {
        Collection<Message> contacts = messageRedisTemplate.opsForSet().members(ALL_MESSAGE_KEY_PREFIX);
        return contacts;
    }

    @Override
    public Message findById(Long id) {
        String key = generateKey(id);
        Message found = messageRedisTemplate.opsForValue().get(key);

        return found;
    }

    @Override
    public void delete(Long id) {

        Message deleted = findAndCheck(id);

        messageRedisTemplate.opsForValue().set(generateKey(deleted.getId()), null);
        messageRedisTemplate.opsForSet().remove(ALL_MESSAGE_KEY_PREFIX, deleted);
    }

    private Message findAndCheck(Long id) {
        Message findById = findById(id);
        return checkNotNull(findById, "No message was found with id:[%s]", id);
    }

    private String generateKey(Long id) {
        return MESSAGE_KEY_PREFIX + checkNotNull(id, "Id must be set!");
    }

}
