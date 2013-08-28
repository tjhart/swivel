package com.tjh.swivel.controller;


import com.tjh.swivel.model.Shunt;
import com.tjh.swivel.model.ShuntRequestHandler;
import org.junit.Test;
import vanderbilt.util.Maps;

import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ConfigurationResourceTest {

    public static final String TARGET_URI = "http:/some/target/uri";
    public static final String RELATIVE_URI = "extra/path";

    @Test
    public void putShuntAddsShuntToRouter() throws URISyntaxException {
        ConfigurationResource configurationResource = new ConfigurationResource();
        RequestRouter mockRouter = mock(RequestRouter.class);
        configurationResource.setRouter(mockRouter);

        configurationResource.putShunt(RELATIVE_URI,
                Maps.asMap(ConfigurationResource.TARGET_URI_KEY, TARGET_URI)
        );

        Shunt expectedShunt = new Shunt(new URI(RELATIVE_URI), new ShuntRequestHandler(new URI(TARGET_URI)));
        verify(mockRouter).setShunt(expectedShunt);
    }
}
