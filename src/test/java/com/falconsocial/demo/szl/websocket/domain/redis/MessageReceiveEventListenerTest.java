package com.falconsocial.demo.szl.websocket.domain.redis;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.falconsocial.demo.szl.websocket.domain.model.Message;
import com.falconsocial.demo.szl.websocket.domain.model.Message.MessageBuilder;
import com.falconsocial.demo.szl.websocket.domain.model.MessageAssertions;
import com.falconsocial.demo.szl.websocket.domain.service.MessageService;

/**
 * Unit test for {@link MessageReceiveEventListener}
 * 
 * @author szabol
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class MessageReceiveEventListenerTest {

    @Mock
    private MessageService mockMessageService;

    private MessageReceiveEventListener tested = new MessageReceiveEventListener();

    @Before
    public void setupMocks() {
        tested.setMessageService(mockMessageService);
    }

    @Test
    public void testHandleMessage() {

        Message testMessage = MessageBuilder.randomInfo().build();
        tested.handleMessage(testMessage, randomAlphabetic(20));

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockMessageService).create(messageCaptor.capture());

        MessageAssertions.assertMessageEquals(testMessage, messageCaptor.getValue());
    }
}
