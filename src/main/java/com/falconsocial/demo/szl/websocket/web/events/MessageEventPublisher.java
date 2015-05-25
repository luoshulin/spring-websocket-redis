package com.falconsocial.demo.szl.websocket.web.events;

import com.falconsocial.demo.szl.websocket.domain.model.Message;

/**
 * Interface which decouples eventing implementation from controller layer
 * 
 * @author szabol
 *
 */
public interface MessageEventPublisher {

    void publishMessageReceived(Message message);

}
