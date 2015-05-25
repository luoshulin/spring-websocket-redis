package com.falconsocial.demo.szl.websocket.web.rest;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.falconsocial.demo.szl.websocket.SpringWebsocketRedisApplication;
import com.falconsocial.demo.szl.websocket.TestUtil;
import com.falconsocial.demo.szl.websocket.domain.model.Message;
import com.falconsocial.demo.szl.websocket.domain.model.Message.MessageBuilder;
import com.falconsocial.demo.szl.websocket.domain.model.MessageAssertions;
import com.falconsocial.demo.szl.websocket.domain.service.MessageService;
import com.falconsocial.demo.szl.websocket.web.events.MessageEventPublisher;

/**
 * Integration tests for {@link MessageController}. In real life this should be split to integration and unit test parts 
 * but now for the sake of simplicity we use this for unit testing also.
 * 
 * @author szabol
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringWebsocketRedisApplication.class)
@WebAppConfiguration
@IntegrationTest
@ActiveProfiles("embedded")
public class MessageControllerIntegrationTest {

    @Mock
    private MessageService mockMessageService;

    @Mock
    private MessageEventPublisher mockMessageEventPublisher;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private SimpMessagingTemplate socketSpy;

    private MockMvc restControllerMockMvc;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);

        socketSpy = Mockito.spy(simpMessagingTemplate);

        MessageController messageController = new MessageController();
        messageController.setBrokerMessagingTemplate(socketSpy);
        messageController.setMessageService(mockMessageService);
        messageController.setMessageEventPublisher(mockMessageEventPublisher);
        this.restControllerMockMvc = MockMvcBuilders.standaloneSetup(messageController).build();
    }

    @Test
    public void testBroadcastPushOnWebsocket() throws Exception {

        Message testMessage = createRandomMessage();
        callBroadcast(testMessage);

        // Asserts
        ArgumentCaptor<Message> sentPayload = ArgumentCaptor.forClass(Message.class);
        verify(socketSpy).convertAndSend(Matchers.eq("/topic/messages"), sentPayload.capture());

        MessageAssertions.assertMessageEquals(testMessage, sentPayload.getValue());
    }

    @Test
    public void testBroadcastPublishesOnRedis() throws Exception {

        Message testMessage = createRandomMessage();

        callBroadcast(testMessage);

        // Asserts
        ArgumentCaptor<Message> sentPayload = ArgumentCaptor.forClass(Message.class);
        verify(mockMessageEventPublisher).publishMessageReceived(sentPayload.capture());

        MessageAssertions.assertMessageEquals(testMessage, sentPayload.getValue());
    }

    @Test
    public void testGetAll() throws Exception {

        List<Message> mockResults = Arrays.asList(createRandomMessage(), createRandomMessage());
        when(mockMessageService.findAll()).thenReturn(mockResults);

        restControllerMockMvc.perform(get("/api/message"))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(mockResults.size())))
                .andExpect(jsonPath("$[0].content").value(mockResults.get(0).getContent()))
                .andExpect(jsonPath("$[0].type").value(mockResults.get(0).getType().toString()))
                .andExpect(status().isOk());

    }

    @Test
    public void testDelete() throws Exception {

        String testId = randomAlphabetic(20);
        when(mockMessageService.findById(testId)).thenReturn(createRandomMessage());

        restControllerMockMvc.perform(delete("/api/message/{id}", testId))
                .andExpect(status().isOk());

        verify(mockMessageService).delete(testId);
    }

    private void callBroadcast(Message testMessage) throws Exception, IOException {

        when(mockMessageService.create(any(Message.class))).thenReturn(testMessage);

        restControllerMockMvc.perform(post("/api/message")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(testMessage)))
                .andExpect(status().isCreated());
    }

    private Message createRandomMessage() {
        return MessageBuilder.randomInfo().build();
    }

}
