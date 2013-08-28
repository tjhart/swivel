package com.tjh.swivel.controller;


import com.tjh.swivel.model.ShuntRequestHandler;
import org.junit.Test;
import vanderbilt.util.Maps;

import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ConfigurationResourceTest {

    public static final String REMOTE_URI = "http:/some/target/uri";
    public static final String LOCAL_URI = "extra/path";

    @Test
    public void putShuntAddsShuntToRouter() throws URISyntaxException {
        ConfigurationResource configurationResource = new ConfigurationResource();
        RequestRouter mockRouter = mock(RequestRouter.class);
        configurationResource.setRouter(mockRouter);

        configurationResource.putShunt(LOCAL_URI, Maps.asMap(ConfigurationResource.REMOTE_URI_KEY, REMOTE_URI));

        verify(mockRouter).setShunt(URI.create(LOCAL_URI), new ShuntRequestHandler(new URI(REMOTE_URI)));
    }
}
