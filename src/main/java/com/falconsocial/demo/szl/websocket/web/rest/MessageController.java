package com.falconsocial.demo.szl.websocket.web.rest;

import java.net.URI;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.falconsocial.demo.szl.websocket.domain.model.Message;
import com.falconsocial.demo.szl.websocket.domain.model.Message.MessageBuilder;
import com.falconsocial.demo.szl.websocket.domain.service.MessageService;
import com.falconsocial.demo.szl.websocket.web.events.MessageEventPublisher;
import com.falconsocial.demo.szl.websocket.web.events.NewMessageBroadcaster;
import com.falconsocial.demo.szl.websocket.web.model.BasicMessage;

@Controller
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageEventPublisher messageEventPublisher;

    // TODO error handling

    /**
     * Rest resource for message broadcast
     */
    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public ResponseEntity<Void> broadcast(@RequestBody BasicMessage message, HttpServletRequest request) {

        Message receivedMessage = createBuilderFromBasicMessage(message)
                .sentBy(request.getRemoteAddr())
                .build();

        // Publish message received event
        messageEventPublisher.publishMessageReceived(receivedMessage);

        return ResponseEntity
                .created(URI.create(getServerUrl(request) + "/api/message/" + receivedMessage.getId()))
                .build();
    }

    /**
     * Rest resource for querying all the persisted messages
     */
    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public ResponseEntity<Collection<Message>> getMessages() {

        Collection<Message> findAll = messageService.findAll();

        return ResponseEntity.ok(findAll);
    }

    /**
     * Rest resource for querying all the persisted messages
     */
    @RequestMapping(value = "/message/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable String id) {

        messageService.delete(id);

        return ResponseEntity.ok().build();
    }

    /**
     * WebSocket channel for receiving {@link BasicMessage} object from websocket clients
     */
    @MessageMapping("/broadcast")
    @SendTo(NewMessageBroadcaster.WEBSOCKET_MESSAGE_TOPIC_PATH)
    public Message socketBroadcast(BasicMessage message) {
        // TODO read client address from websocket session
        return createBuilderFromBasicMessage(message).build();
    }

    private MessageBuilder createBuilderFromBasicMessage(BasicMessage message) {
        return MessageBuilder.empty()
                .withContent(message.getContent())
                .withType(message.getType());
    }

    private String getServerUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    protected void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    protected void setMessageEventPublisher(MessageEventPublisher messageEventPublisher) {
        this.messageEventPublisher = messageEventPublisher;
    }

}
