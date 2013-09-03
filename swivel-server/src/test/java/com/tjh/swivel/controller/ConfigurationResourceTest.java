package com.tjh.swivel.controller;


import com.tjh.swivel.model.ShuntRequestHandler;
import org.junit.Before;
import org.junit.Test;
import vanderbilt.util.Maps;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConfigurationResourceTest {

    public static final String REMOTE_URI = "http:/some/target/uri";
    public static final Map<String, String> SHUNT_JSON =
            Maps.asConstantMap(ConfigurationResource.REMOTE_URI_KEY, REMOTE_URI);
    public static final String LOCAL_PATH = "extra/path";
    public static final URI LOCAL_URI = URI.create(LOCAL_PATH);
    private ConfigurationResource configurationResource;
    private RequestRouter mockRouter;
    private HttpServletRequest mockRequest;
    public static final Map<String, Object> JSON = Collections.emptyMap();

    @Before
    public void setUp() throws Exception {
        configurationResource = new ConfigurationResource();
        mockRouter = mock(RequestRouter.class);
        mockRequest = mock(HttpServletRequest.class);

        configurationResource.setRouter(mockRouter);
    }

    @Test
    public void putShuntAddsShuntToRouter() throws URISyntaxException {
        configurationResource.putShunt(LOCAL_URI, SHUNT_JSON);

        verify(mockRouter).setShunt(LOCAL_URI, new ShuntRequestHandler(new URI(REMOTE_URI)));
    }

    @Test
    public void deleteShuntRemovesShuntFromRouter() throws URISyntaxException {
        configurationResource.deleteShunt(LOCAL_URI);

        verify(mockRouter).deleteShunt(LOCAL_URI);
    }

    @Test
    public void putStubDefersToRouter() throws URISyntaxException, ScriptException {
        configurationResource.putStub(LOCAL_PATH, JSON, mockRequest);

        verify(mockRouter).addStub(LOCAL_URI, JSON);
    }

    @Test
    public void putStubAddsQueryStringIfProvided() throws URISyntaxException, ScriptException {
        when(mockRequest.getQueryString()).thenReturn("key=val");

        configurationResource.putStub(LOCAL_PATH, JSON, mockRequest);

        verify(mockRouter).addStub(new URI(LOCAL_PATH + "?" + "key=val"), JSON);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void putStubReturnsRouterIDForStub() throws URISyntaxException, ScriptException {
        when(mockRouter.addStub(any(URI.class), any(Map.class))).thenReturn("someId");

        assertThat((String) configurationResource.putStub(LOCAL_PATH, JSON, mockRequest).get("id"), equalTo("someId"));
    }
}
