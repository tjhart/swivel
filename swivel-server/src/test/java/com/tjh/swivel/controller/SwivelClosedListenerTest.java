package com.tjh.swivel.controller;

import com.tjh.swivel.model.Configuration;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SwivelClosedListenerTest {

    private SwivelClosedListener swivelClosedListener;
    private ObjectMapper mockObjectMapper;
    private Configuration mockConfiguration;
    private File mockFile;

    @Before
    public void setUp() throws Exception {
        swivelClosedListener = new SwivelClosedListener();
        mockObjectMapper = mock(ObjectMapper.class);
        mockConfiguration = mock(Configuration.class);
        mockFile = mock(File.class);

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
