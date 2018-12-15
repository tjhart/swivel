package com.tjh.swivel.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjh.swivel.model.Configuration;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConfigurationResourceTest {

    public static final String LOCAL_PATH = "extra/path";
    public static final URI LOCAL_URI = URI.create(LOCAL_PATH);
    private ConfigurationResource configurationResource;
    private Configuration mockConfiguration;
    private ObjectMapper mockObjectMapper;
    private Map<String,Map<String,Object>> configurationMap;

    @Before
    public void setUp() {
        configurationResource = new ConfigurationResource();
        mockConfiguration = mock(Configuration.class);
        configurationMap = Collections.emptyMap();
        mockObjectMapper = mock(ObjectMapper.class);

        configurationResource.setConfiguration(mockConfiguration);
        configurationResource.setObjectMapper(mockObjectMapper);

        when(mockConfiguration.toMap()).thenReturn(configurationMap);
    }

    @Test
    public void resetDefersToConfiguration() {
        configurationResource.reset();

        verify(mockConfiguration).reset();
    }

    @Test
    public void resetReturnsConfigurationMap() {
        assertThat(configurationResource.reset(), sameInstance(configurationMap));

        verify(mockConfiguration).toMap();
    }

    @Test
    public void deletePathDefersToConfiguration(){
        configurationResource.deletePath(LOCAL_URI);

        verify(mockConfiguration).removePath(LOCAL_URI);
    }

    @Test
    public void getConfigurationResponseConvertsConfigurationMapToJSON() throws IOException {
        configurationResource.getConfigurationResponse();

        verify(mockObjectMapper).writeValue(any(Writer.class), eq(configurationMap));
    }

    @Test
    public void getConfigurationResponseIncludesContentDispositionHeader() throws IOException {
        Response response = configurationResource.getConfigurationResponse();

        assertThat((String)response.getMetadata().getFirst("Content-Disposition"),
                containsString("attachment; filename=\""));
    }
}
