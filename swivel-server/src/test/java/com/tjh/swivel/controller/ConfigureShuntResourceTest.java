package com.tjh.swivel.controller;

import com.tjh.swivel.model.Configuration;
import com.tjh.swivel.model.ShuntRequestHandler;
import org.junit.Before;
import org.junit.Test;
import vanderbilt.util.Maps;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ConfigureShuntResourceTest {

    public static final String REMOTE_URI = "http:/some/target/uri";
    public static final Map<String, String> SHUNT_JSON =
            Maps.asConstantMap(ConfigureShuntResource.REMOTE_URL_KEY, REMOTE_URI);

    public static final String LOCAL_PATH = "extra/path";
    public static final URI LOCAL_URI = URI.create(LOCAL_PATH);
    private ConfigureShuntResource configureShuntResource;
    private Configuration mockConfiguration;

    @Before
    public void setUp() throws Exception {
        configureShuntResource = new ConfigureShuntResource();
        mockConfiguration = mock(Configuration.class);

        configureShuntResource.setConfiguration(mockConfiguration);
    }

    @Test
    public void putShuntAddsShuntToRouter() throws URISyntaxException, MalformedURLException {
        configureShuntResource.putShunt(LOCAL_URI, SHUNT_JSON);

        verify(mockConfiguration).setShunt(LOCAL_URI, new ShuntRequestHandler(new URL(REMOTE_URI)));
    }

    @Test
    public void deleteShuntRemovesShuntFromRouter() throws URISyntaxException {
        configureShuntResource.deleteShunt(LOCAL_URI);

        verify(mockConfiguration).deleteShunt(LOCAL_URI);
    }
}
