package com.tjh.swivel.model;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class WrapperServletInputStreamTest {

    public static final String CONTENT = "content";
    private WrapperServletInputStream inputStream;

    @Before
    public void setUp() throws Exception {
        inputStream = new WrapperServletInputStream(CONTENT);
    }

    @Test
    public void readWorks() throws IOException {
        byte[] bytes = new byte[1024];
        int pos = 0;
        while ((bytes[pos] = (byte) inputStream.read()) != -1) {
            pos++;
        }
        bytes[pos] = 0;

        assertThat(new String(bytes).trim(), equalTo(CONTENT));
    }

    @Test
    public void readLineWorks() throws IOException {
        byte[] bytes = new byte[1024];
        inputStream.readLine(bytes, 0, bytes.length);

        assertThat(new String(bytes).trim(), equalTo(CONTENT));
    }
}
