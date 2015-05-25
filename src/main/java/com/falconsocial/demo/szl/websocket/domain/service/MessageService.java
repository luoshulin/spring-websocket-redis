package com.falconsocial.demo.szl.websocket.domain.service;

import java.util.Collection;

import com.falconsocial.demo.szl.websocket.domain.model.Message;

/**
 * Interface which defines domain methods connected to {@link Message} objects
 * 
 * @author szabol
 *
 */
public interface MessageService {

    /**
     * Saves a new {@link Message} with a generated ID. Ignores the id property of the message parameter.
     * 
     * @param message Saved message
     * @return The id of the saved {@link Message}
     * 
     */
    Message create(Message message);

    /**
     * Finds all the messages.
     */
    Collection<Message> findAll();

    /**
     * Returns a {@link Message} by id.
     * 
     * @param id The queried id
     * @return The message with the given id or <code>null</code> if not found.
     */
    Message findById(String id);

    /**
     * Deletes the message with the given id.
     * 
     * @throws IllegalStateException if no message was found with the given id.
     */
    void delete(String id);

}
