package org.github.felipegutierrez.explore;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HelloWorldTest {

    private final HelloWorld subject = new HelloWorld();

    @Test
    public void testGetMessage() {
        assertEquals("Hello World!", subject.getMessage(false));
    }
}
