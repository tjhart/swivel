package com.tjh.swivel.controller;

import com.tjh.swivel.model.StubFactory;
import com.tjh.swivel.model.StubRequestHandler;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
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
    private RequestRouter mockRouter;
    private HttpServletRequest mockRequest;
    public static final Map<String, Object> JSON = Collections.emptyMap();
    private StubFactory mockStubFactory;
    private StubRequestHandler mockStubRequestHandler;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        configureStubResource = new ConfigureStubResource();
        mockRouter = mock(RequestRouter.class);
        mockRequest = mock(HttpServletRequest.class);
        mockStubFactory = mock(StubFactory.class);
        mockStubRequestHandler = mock(StubRequestHandler.class);

        configureStubResource.setRouter(mockRouter);
        configureStubResource.setStubFactory(mockStubFactory);
        ConfigurationResource mockConfigurationResource = mock(ConfigurationResource.class);
        configureStubResource.setConfigurationResource(mockConfigurationResource);

        when(mockStubFactory.createStubFor(any(URI.class), anyMap())).thenReturn(mockStubRequestHandler);
        when(mockStubRequestHandler.getId()).thenReturn(STUB_HANDLER_ID);
    }

    @Test
    public void putStubDefersToFactory() throws ScriptException, URISyntaxException {
        configureStubResource.postStub(LOCAL_PATH, JSON, mockRequest);

        verify(mockStubFactory).createStubFor(LOCAL_URI, JSON);
    }

    @Test
    public void putStubDefersToRouter() throws URISyntaxException, ScriptException {
        configureStubResource.postStub(LOCAL_PATH, JSON, mockRequest);

        verify(mockRouter).addStub(LOCAL_URI, mockStubRequestHandler);
    }

    @Test
    public void putStubAddsQueryStringIfProvided() throws URISyntaxException, ScriptException {
        when(mockRequest.getQueryString()).thenReturn("key=val");

        configureStubResource.postStub(LOCAL_PATH, JSON, mockRequest);

        verify(mockRouter).addStub(new URI(LOCAL_PATH + "?" + "key=val"), mockStubRequestHandler);
    }

    @Test
    public void putStubReturnsRouterIDForStub() throws URISyntaxException, ScriptException {

        assertThat((Integer) configureStubResource.postStub(LOCAL_PATH, JSON, mockRequest).get("id"),
                equalTo(STUB_HANDLER_ID));
    }

    @Test
    public void deleteStubRemovesStubAtURI() throws ScriptException, URISyntaxException {
        configureStubResource.postStub(LOCAL_PATH, JSON, mockRequest);

        configureStubResource.deleteStub(LOCAL_URI, STUB_HANDLER_ID);

        verify(mockRouter).removeStub(LOCAL_URI, STUB_HANDLER_ID);
    }

}
