package com.tjh.swivel.controller;

import com.tjh.swivel.model.Configuration;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SwivelListenerTest {
    public static final String SWIVEL_FILE_PATH = "/tmp/file.json";
    private SwivelListener swivelListener;
    private ConfigurableEnvironment mockEnv;
    private ObjectMapper mockObjectMapper;
    private Configuration mockConfiguration;

    @Before
    public void setUp() throws Exception {
        swivelListener = new SwivelListener();
        mockEnv = mock(ConfigurableEnvironment.class);
        mockObjectMapper = mock(ObjectMapper.class);
        mockConfiguration = mock(Configuration.class);

        swivelListener.setEnv(mockEnv);
        swivelListener.setObjectMapper(mockObjectMapper);
        swivelListener.setConfiguration(mockConfiguration);

        when(mockEnv.getProperty("java.io.tmpdir"))
                .thenReturn(System.getProperty("java.io.tmpdir"));
    }

    @Test
    public void getSaveFileSetsSaveFileWithPropertyIfAvailable() {
        when(mockEnv.getProperty(SwivelClosedListener.FILE_KEY))
                .thenReturn(SWIVEL_FILE_PATH);

        assertThat(swivelListener
                .getSaveFile()
                .getPath(),
                equalTo(SWIVEL_FILE_PATH));

        verify(mockEnv).getProperty(SwivelClosedListener.FILE_KEY);
    }

    @Test
    public void getSaveFileSetsSaveFileWithTmpDirIfFileKeyNotFound() {
        assertThat(swivelListener
                .getSaveFile()
                .getPath(),
                equalTo(System.getProperty("java.io.tmpdir") + SwivelClosedListener.DEFAULT_FILE_NAME));

        verify(mockEnv).getProperty(SwivelClosedListener.FILE_KEY);
        verify(mockEnv).getProperty("java.io.tmpdir");
    }
}
