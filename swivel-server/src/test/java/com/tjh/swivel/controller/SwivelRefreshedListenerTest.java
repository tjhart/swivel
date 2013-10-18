package com.tjh.swivel.controller;

import com.tjh.swivel.model.Configuration;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SwivelRefreshedListenerTest {

    private SwivelRefreshedListener swivelRefreshedListener;
    private Configuration mockConfiguration;
    private ObjectMapper mockObjectMapper;
    private File mockFile;

    @Before
    public void setUp() throws Exception {
        swivelRefreshedListener = new SwivelRefreshedListener();
        mockConfiguration = mock(Configuration.class);
        mockObjectMapper = mock(ObjectMapper.class);

        swivelRefreshedListener.setConfiguration(mockConfiguration);
        swivelRefreshedListener.setObjectMapper(mockObjectMapper);

        swivelRefreshedListener = spy(swivelRefreshedListener);
        mockFile = mock(File.class);

        doReturn(mockFile)
                .when(swivelRefreshedListener)
                .getSaveFile();

        when(mockFile.exists()).thenReturn(true);
    }

    @Test
    public void onApplicationStartLoadsConfiguration() throws IOException {
        swivelRefreshedListener.onApplicationEvent(null);

        verify(swivelRefreshedListener).getSaveFile();
        verify(mockObjectMapper).readValue(mockFile, Map.class);
        verify(mockConfiguration).load(anyMap());
    }

}
