package com.tjh.swivel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjh.swivel.model.Configuration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class SwivelClosedListenerTest {

    private SwivelClosedListener swivelClosedListener;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private ObjectMapper mockObjectMapper;
    @Mock
    private Configuration mockConfiguration;
    @Mock
    private File mockFile;

    @Before
    public void setUp() {
        swivelClosedListener = new SwivelClosedListener();

        swivelClosedListener.setObjectMapper(mockObjectMapper);
        swivelClosedListener.setConfiguration(mockConfiguration);
        swivelClosedListener.setSaveFile(mockFile);

    }

    @Test
    public void onApplicationEventWritesSaveFile() throws IOException {
        swivelClosedListener.onApplicationEvent(null);

        verify(mockConfiguration).toMap();
        verify(mockObjectMapper).writeValue(eq(mockFile), any(Map.class));
    }
}
