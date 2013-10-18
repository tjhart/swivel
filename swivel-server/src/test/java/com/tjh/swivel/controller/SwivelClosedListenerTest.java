package com.tjh.swivel.controller;

import com.tjh.swivel.model.Configuration;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SwivelClosedListenerTest {

    public static final String SWIVEL_FILE_PATH = "/tmp/file.json";
    private SwivelClosedListener swivelClosedListener;
    private ConfigurableEnvironment mockEnv;
    private ObjectMapper mockObjectMapper;
    private Configuration mockConfiguration;

    @Before
    public void setUp() throws Exception {
        swivelClosedListener = new SwivelClosedListener();
        mockEnv = mock(ConfigurableEnvironment.class);
        mockObjectMapper = mock(ObjectMapper.class);
        mockConfiguration = mock(Configuration.class);

        swivelClosedListener.setEnv(mockEnv);
        swivelClosedListener.setObjectMapper(mockObjectMapper);
        swivelClosedListener.setConfiguration(mockConfiguration);

        when(mockEnv.getProperty("java.io.tmpdir"))
                .thenReturn(System.getProperty("java.io.tmpdir"));
    }

    @Test
    public void getSaveFileSetsSaveFileWithPropertyIfAvailable() {
        when(mockEnv.getProperty(SwivelClosedListener.FILE_KEY))
                .thenReturn(SWIVEL_FILE_PATH);

        assertThat(swivelClosedListener
                .getSaveFile()
                .getPath(),
                equalTo(SWIVEL_FILE_PATH));

        verify(mockEnv).getProperty(SwivelClosedListener.FILE_KEY);
    }

    @Test
    public void getSaveFileSetsSaveFileWithTmpDirIfFileKeyNotFound() {
        assertThat(swivelClosedListener
                .getSaveFile()
                .getPath(),
                equalTo(System.getProperty("java.io.tmpdir") + SwivelClosedListener.DEFAULT_FILE_NAME));

        verify(mockEnv).getProperty(SwivelClosedListener.FILE_KEY);
        verify(mockEnv).getProperty("java.io.tmpdir");
    }

    @Test
    public void onApplicationEventWritesSaveFile() throws IOException {
        swivelClosedListener.onApplicationEvent(null);

        verify(mockConfiguration).toMap();
        verify(mockObjectMapper).writeValue(any(File.class), any(Map.class));

        System.out.println("mockConfiguration.toMap() = " + mockConfiguration.toMap());
    }
}
