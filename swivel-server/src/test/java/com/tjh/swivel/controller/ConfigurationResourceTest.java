package com.tjh.swivel.controller;


import com.tjh.swivel.model.ShuntRequestHandler;
import org.junit.Before;
import org.junit.Test;
import vanderbilt.util.Maps;

import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ConfigurationResourceTest {

    public static final String REMOTE_URI = "http:/some/target/uri";
    public static final String LOCAL_PATH = "extra/path";
    public static final URI LOCAL_URI = URI.create(LOCAL_PATH);
    private ConfigurationResource configurationResource;
    private RequestRouter mockRouter;

    @Before
    public void setUp() throws Exception {
        configurationResource = new ConfigurationResource();
        mockRouter = mock(RequestRouter.class);

        configurationResource.setRouter(mockRouter);
    }

    @Test
    public void putShuntAddsShuntToRouter() throws URISyntaxException {
        configurationResource.putShunt(LOCAL_PATH, Maps.asMap(ConfigurationResource.REMOTE_URI_KEY, REMOTE_URI));

        verify(mockRouter).setShunt(LOCAL_URI, new ShuntRequestHandler(new URI(REMOTE_URI)));
    }

    @Test
    public void deleteShuntRemovesShuntFromRouter() throws URISyntaxException {
        configurationResource.deleteShunt(LOCAL_PATH);

        verify(mockRouter).deleteShunt(LOCAL_URI);
    }
}
