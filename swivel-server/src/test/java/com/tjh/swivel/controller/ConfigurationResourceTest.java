package com.tjh.swivel.controller;


import com.tjh.swivel.model.Configuration;
import com.tjh.swivel.model.ShuntRequestHandler;
import org.junit.Before;
import org.junit.Test;
import vanderbilt.util.Maps;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConfigurationResourceTest {

    public static final String LOCAL_PATH = "extra/path";
    private ConfigurationResource configurationResource;
    private Configuration mockConfiguration;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        configurationResource = new ConfigurationResource();
        mockConfiguration = mock(Configuration.class);

        configurationResource.setConfiguration(mockConfiguration);
    }

    @Test
    public void getConfigDefersToConfiguration() {
        configurationResource.getConfigurationMap();

        verify(mockConfiguration).getUriHandlers();
    }

    @Test
    public void getConfigTranslatesShuntsToStrings() {
        ShuntRequestHandler mockShuntHandler = mock(ShuntRequestHandler.class);
        when(mockConfiguration.getUriHandlers()).thenReturn(
                Maps.asMap(LOCAL_PATH, Maps.<String, Object>asMap(Configuration.SHUNT_NODE, mockShuntHandler)));

        Map<String, Map<String, Object>> configuration = configurationResource.getConfigurationMap();

        assertThat(configuration.get(LOCAL_PATH).get("shunt"), equalTo((Object) mockShuntHandler.toMap()));
    }
}
