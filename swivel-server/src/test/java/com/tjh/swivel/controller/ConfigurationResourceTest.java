package com.tjh.swivel.controller;


import com.tjh.swivel.model.Configuration;
import org.junit.Before;

import static org.mockito.Mockito.mock;

public class ConfigurationResourceTest {

    public static final String LOCAL_PATH = "extra/path";
    private ConfigurationResource configurationResource;
    private Configuration mockConfiguration;

    @Before
    public void setUp() throws Exception {
        configurationResource = new ConfigurationResource();
        mockConfiguration = mock(Configuration.class);

        configurationResource.setConfiguration(mockConfiguration);
    }
}
