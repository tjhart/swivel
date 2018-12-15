package com.tjh.swivel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjh.swivel.model.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SwivelRefreshedListenerTest {

    private SwivelRefreshedListener swivelRefreshedListener;
    private Configuration mockConfiguration;
    private ObjectMapper mockObjectMapper;
    private File mockFile;
    private Map<String, Map<String, Object>> mockMap;

    @Before
    public void setUp() throws IOException {
        swivelRefreshedListener = new SwivelRefreshedListener();
        mockConfiguration = mock(Configuration.class);
        mockObjectMapper = mock(ObjectMapper.class);
        mockFile = mock(File.class);
        //noinspection unchecked
        mockMap = mock(Map.class);

        swivelRefreshedListener.setConfiguration(mockConfiguration);
        swivelRefreshedListener.setObjectMapper(mockObjectMapper);
        swivelRefreshedListener.setSaveFile(mockFile);

        when(mockFile.exists()).thenReturn(true);
        when(mockObjectMapper.readValue(mockFile, Map.class))
                .thenReturn(mockMap);
    }

    @Test
    public void onApplicationStartLoadsConfiguration() throws IOException {
        swivelRefreshedListener.onApplicationEvent(null);

        verify(mockObjectMapper).readValue(mockFile, Map.class);
        verify(mockConfiguration).load(mockMap);
    }

}
