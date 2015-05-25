package com.falconsocial.demo.szl.websocket.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.falconsocial.demo.szl.websocket.domain.model.Message;
import com.google.common.collect.ImmutableList;

/**
 * Basic in-memory {@link MessageService} implementation. Can be activated with spring profile "embedded"
 * 
 * @author szabol
 *
 */
@Service
@Profile("embedded")
public class TestMessageServiceImpl implements MessageService {

    private long id = 0;

    private List<Message> messageStore = new ArrayList<>();

    @Override
    public Message create(Message message) {
        message.setId(id++);
        messageStore.add(message);

        return message;
    }

    @Override
    public Collection<Message> findAll() {
        return ImmutableList.copyOf(messageStore);
    }

    @Override
    public Message findById(Long id) {
        return messageStore.stream()
                .filter(idEquals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void delete(Long id) {
        messageStore.stream()
                .filter(idEquals(id))
                .forEach(messageStore::remove);

    }

    private static Predicate<? super Message> idEquals(Long id) {
        return m -> m.getId().equals(id);
    }
}
