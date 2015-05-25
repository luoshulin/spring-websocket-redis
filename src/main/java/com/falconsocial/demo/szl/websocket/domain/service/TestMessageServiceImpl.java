package com.falconsocial.demo.szl.websocket.domain.service;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.falconsocial.demo.szl.websocket.domain.model.Message;
import com.falconsocial.demo.szl.websocket.domain.redis.MessageReceiveEventListener;
import com.falconsocial.demo.szl.websocket.web.events.MessageEventPublisher;
import com.google.common.collect.ImmutableList;

/**
 * Basic in-memory {@link MessageService} implementation, which also implements {@link MessageEventPublisher} interface. Handy for testing or when Redis is not available.
 * Can be activated with spring profile "embedded"
 * 
 * @author szabol
 *
 */
@Service
@Profile("embedded")
public class TestMessageServiceImpl implements MessageService, MessageEventPublisher {

    @Autowired
    private MessageReceiveEventListener receiveEventListener;

    private List<Message> messageStore = new ArrayList<>();

    @Override
    public Message create(Message message) {
        messageStore.add(message);

        return message;
    }

    @Override
    public Collection<Message> findAll() {
        return ImmutableList.copyOf(messageStore);
    }

    @Override
    public Message findById(String id) {
        return messageStore.stream()
                .filter(idEquals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void delete(String id) {
        boolean removeIf = messageStore.removeIf(idEquals(id));
        checkState(removeIf, "No message was found with id:[%s]!", id);
    }

    private static Predicate<? super Message> idEquals(String id) {
        return m -> m.getId().equals(id);
    }

    @Override
    public void publishMessageReceived(Message message) {

        create(message);

    }

}
