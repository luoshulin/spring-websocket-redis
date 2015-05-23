package com.falconsocial.demo.szl.websocket.web.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.falconsocial.demo.szl.websocket.model.PushMessage;
import com.falconsocial.demo.szl.websocket.model.Message;

@Controller
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private SimpMessagingTemplate brokerMessagingTemplate;

    /**
     * WebSocket controller channel for receiving {@link Message} object from websocket clients
     * 
     * @param message
     * @return
     * @throws InterruptedException 
     */
    @MessageMapping("/broadcast")
    @SendTo("/topic/messages")
    public PushMessage socketBroadcast(Message message) throws InterruptedException {
        // TODO read client address from websocket session
        return new PushMessage(message, "dummy");
    }

    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public ResponseEntity<Void> broadcast(@RequestBody Message message, HttpServletRequest request) throws InterruptedException {

        PushMessage broadcastMessage = new PushMessage(message, request.getRemoteAddr());
        brokerMessagingTemplate.convertAndSend("/topic/messages", broadcastMessage);

        return ResponseEntity.ok().build();
    }

    protected void setBrokerMessagingTemplate(SimpMessagingTemplate brokerMessagingTemplate) {
        this.brokerMessagingTemplate = brokerMessagingTemplate;
    }
}
