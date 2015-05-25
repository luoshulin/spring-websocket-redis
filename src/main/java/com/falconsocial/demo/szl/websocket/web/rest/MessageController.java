package com.falconsocial.demo.szl.websocket.web.rest;

import java.net.URI;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.falconsocial.demo.szl.websocket.domain.model.Message;
import com.falconsocial.demo.szl.websocket.domain.service.MessageService;
import com.falconsocial.demo.szl.websocket.web.model.BasicMessage;

@Controller
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private SimpMessagingTemplate brokerMessagingTemplate;

    @Autowired
    private MessageService messageService;

    //TODO error handling
    
    /**
     * Rest resource for message broadcast
     */
    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public ResponseEntity<Void> broadcast(@RequestBody BasicMessage message, HttpServletRequest request) {

        Message broadcastMessage = new Message(message, request.getRemoteAddr());

        Message create = messageService.create(broadcastMessage);

        brokerMessagingTemplate.convertAndSend("/topic/messages", broadcastMessage);

        return ResponseEntity
                .created(URI.create(getServerUrl(request) + "/api/message/" + create.getId()))
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
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        messageService.delete(id);

        return ResponseEntity.ok().build();
    }

    /**
     * WebSocket channel for receiving {@link BasicMessage} object from websocket clients
     */
    @MessageMapping("/broadcast")
    @SendTo("/topic/messages")
    public Message socketBroadcast(BasicMessage message) {
        // TODO read client address from websocket session
        return new Message(message, "dummy");
    }

    private String getServerUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    protected void setBrokerMessagingTemplate(SimpMessagingTemplate brokerMessagingTemplate) {
        this.brokerMessagingTemplate = brokerMessagingTemplate;
    }

    protected void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
}
