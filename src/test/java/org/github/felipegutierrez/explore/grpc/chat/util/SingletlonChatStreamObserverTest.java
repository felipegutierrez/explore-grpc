package org.github.felipegutierrez.explore.grpc.chat.util;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class SingletlonChatStreamObserverTest {

    @Test
    public void testSingletonHashCode() {
        SingletlonChatStreamObserver singleton01 = SingletlonChatStreamObserver.getInstance();
        SingletlonChatStreamObserver singleton02 = SingletlonChatStreamObserver.getInstance();

        assertEquals(singleton01.hashCode(), singleton02.hashCode());
    }
}
