package com.tjh.swivel.controller;

import com.tjh.swivel.model.Configuration;
import com.tjh.swivel.model.ShuntRequestHandler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;

import static org.mockito.Mockito.verify;

public class ConfigureShuntResourceTest {

    public static final String REMOTE_URI = "http:/some/target/uri";
    public static final Map<String, String> SHUNT_JSON =
            Map.of(ConfigureShuntResource.REMOTE_URL_KEY, REMOTE_URI);

    public static final String LOCAL_PATH = "extra/path";
    public static final URI LOCAL_URI = URI.create(LOCAL_PATH);
    private ConfigureShuntResource configureShuntResource;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private Configuration mockConfiguration;

    @Before
    public void setUp() {
        configureShuntResource = new ConfigureShuntResource();

        configureShuntResource.setConfiguration(mockConfiguration);
    }

    @Test
    public void putShuntAddsShuntToRouter() throws MalformedURLException {
        configureShuntResource.putShunt(LOCAL_URI, SHUNT_JSON);

        verify(mockConfiguration).setShunt(LOCAL_URI, new ShuntRequestHandler(SHUNT_JSON));
    }

    @Test
    public void deleteShuntRemovesShuntFromRouter() {
        configureShuntResource.deleteShunt(LOCAL_URI);

        verify(mockConfiguration).deleteShunt(LOCAL_URI);
    }
}
