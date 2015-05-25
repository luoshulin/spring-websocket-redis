package com.falconsocial.demo.szl.websocket.domain.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MessageAssertions {

    public static void assertMessageEquals(Message testMessage, Message captured) {
        assertEquals(testMessage.getContent(), captured.getContent());
        assertEquals(testMessage.getType(), captured.getType());
        assertNotNull(captured.getSender());
        assertNotNull(captured.getDate());
    }
    
}
