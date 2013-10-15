package com.tjh.swivel.controller;

import com.tjh.swivel.model.Configuration;
import com.tjh.swivel.model.StubFactory;
import com.tjh.swivel.model.StubRequestHandler;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConfigureStubResourceTest {
    public static final String LOCAL_PATH = "extra/path";
    public static final URI LOCAL_URI = URI.create(LOCAL_PATH);
    public static final int STUB_HANDLER_ID = 123;
    private ConfigureStubResource configureStubResource;
    private Configuration mockConfiguration;
    private Map<String, Object> json = new HashMap<String, Object>();
    private StubFactory mockStubFactory;
    private StubRequestHandler mockStubRequestHandler;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        configureStubResource = new ConfigureStubResource();
        mockConfiguration = mock(Configuration.class);
        mockStubFactory = mock(StubFactory.class);
        mockStubRequestHandler = mock(StubRequestHandler.class);

        configureStubResource.setConfiguration(mockConfiguration);
        configureStubResource.setStubFactory(mockStubFactory);

        when(mockStubFactory.createStubFor(any(URI.class), anyMap())).thenReturn(mockStubRequestHandler);
        when(mockStubRequestHandler.getId()).thenReturn(STUB_HANDLER_ID);
    }

    @Test
    public void putStubDefersToFactory() throws ScriptException, URISyntaxException {
        configureStubResource.postStub(LOCAL_PATH, json);

        verify(mockStubFactory).createStubFor(LOCAL_URI, json);
    }

    @Test
    public void putStubDefersToRouter() throws URISyntaxException, ScriptException {
        configureStubResource.postStub(LOCAL_PATH, json);

        verify(mockConfiguration).addStub(LOCAL_URI, mockStubRequestHandler);
    }

    @Test
    public void putStubReturnsRouterIDForStub() throws URISyntaxException, ScriptException {

        assertThat((Integer) configureStubResource.postStub(LOCAL_PATH, json).get("id"),
                equalTo(STUB_HANDLER_ID));
    }

    @Test
    public void deleteStubRemovesStubAtURI() throws ScriptException, URISyntaxException {
        configureStubResource.postStub(LOCAL_PATH, json);

        configureStubResource.deleteStub(LOCAL_URI, STUB_HANDLER_ID);

        verify(mockConfiguration).removeStub(LOCAL_URI, STUB_HANDLER_ID);
    }
}
