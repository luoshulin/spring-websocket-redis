package com.falconsocial.demo.szl.websocket.web.rest;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.falconsocial.demo.szl.websocket.SpringWebsocketRedisApplication;
import com.falconsocial.demo.szl.websocket.TestUtil;
import com.falconsocial.demo.szl.websocket.model.Message;
import com.falconsocial.demo.szl.websocket.model.MessageType;
import com.falconsocial.demo.szl.websocket.model.PushMessage;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringWebsocketRedisApplication.class)
@WebAppConfiguration
@IntegrationTest
public class MessageControllerTest {

    // @Mock
    // private SimpMessagingTemplate mockSimpMessagingTemplate;

    private SimpMessagingTemplate socketSpy;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private MockMvc restControllerMockMvc;

    @Before
    public void setup() {
        socketSpy = Mockito.spy(simpMessagingTemplate);

        MessageController messageController = new MessageController();
        messageController.setBrokerMessagingTemplate(socketSpy);
        this.restControllerMockMvc = MockMvcBuilders.standaloneSetup(messageController).build();
    }

    @Test
    public void testBroadcast() throws Exception {

        MessageType testType = MessageType.INFO;
        String testContent = randomAlphabetic(200);
        Message message = new Message(testContent, testType);

        restControllerMockMvc.perform(post("/api/message")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(message)))
                .andExpect(status().isOk());

        //Asserts
        ArgumentCaptor<PushMessage> sentPayload = ArgumentCaptor.forClass(PushMessage.class);
        verify(socketSpy).convertAndSend(Matchers.eq("/topic/messages"), sentPayload.capture());
        
        PushMessage captured = sentPayload.getValue();
        assertEquals(testContent, captured.getContent());
        assertEquals(testType, captured.getType());
        assertNotNull(captured.getDate());
        assertNotNull(captured.getSender());
    }
}
