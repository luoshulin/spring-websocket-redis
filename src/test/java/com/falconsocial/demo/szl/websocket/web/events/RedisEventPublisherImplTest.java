package com.falconsocial.demo.szl.websocket.web.events;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.redis.core.RedisTemplate;

import com.falconsocial.demo.szl.websocket.domain.model.Message;
import com.falconsocial.demo.szl.websocket.domain.model.Message.MessageBuilder;
import com.falconsocial.demo.szl.websocket.domain.model.MessageAssertions;
import com.falconsocial.demo.szl.websocket.domain.redis.NewMessageEventListener;

/**
 * Unit test for {@link RedisEventPublisher}
 * 
 * @author szabol
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RedisEventPublisherImplTest {

    @Mock
    private RedisTemplate<String, Message> mockRedisTemplate;

    private RedisMessageEventPublisherImpl tested = new RedisMessageEventPublisherImpl();

    @Before
    public void setup() {
        tested.setMessageRedisTemplate(mockRedisTemplate);
    }

    @Test
    public void testPublishReceive() {

        Message testMessage = MessageBuilder.randomInfo().build();
        tested.publishMessageReceived(testMessage);

        ArgumentCaptor<Message> publishedMessage = ArgumentCaptor.forClass(Message.class);
        verify(mockRedisTemplate).convertAndSend(eq(NewMessageEventListener.EVENT_RECEIVE_MESSAGE_KEY), publishedMessage.capture());

        MessageAssertions.assertMessageEquals(testMessage, publishedMessage.getValue());
    }

    @Test(expected = NullPointerException.class)
    public void testEmptyPublishThrowsError() {
        tested.publishMessageReceived(null);
    }
}
