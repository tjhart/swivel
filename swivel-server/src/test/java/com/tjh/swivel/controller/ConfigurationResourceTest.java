package com.tjh.swivel.controller;


import com.tjh.swivel.model.ShuntRequestHandler;
import com.tjh.swivel.model.StubFactory;
import com.tjh.swivel.model.StubRequestHandler;
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
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConfigurationResourceTest {

    public static final String REMOTE_URI = "http:/some/target/uri";
    public static final Map<String, String> SHUNT_JSON =
            Maps.asConstantMap(ConfigurationResource.REMOTE_URI_KEY, REMOTE_URI);
    public static final String LOCAL_PATH = "extra/path";
    public static final URI LOCAL_URI = URI.create(LOCAL_PATH);
    public static final int STUB_HANDLER_ID = 123;
    private ConfigurationResource configurationResource;
    private RequestRouter mockRouter;
    private HttpServletRequest mockRequest;
    public static final Map<String, Object> JSON = Collections.emptyMap();
    private StubFactory mockStubFactory;
    private StubRequestHandler mockStubRequestHandler;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        configurationResource = new ConfigurationResource();
        mockRouter = mock(RequestRouter.class);
        mockRequest = mock(HttpServletRequest.class);
        mockStubFactory = mock(StubFactory.class);
        mockStubRequestHandler = mock(StubRequestHandler.class);

        configurationResource.setRouter(mockRouter);
        configurationResource.setStubFactory(mockStubFactory);

        when(mockStubFactory.createStubFor(any(URI.class), anyMap())).thenReturn(mockStubRequestHandler);
        when(mockStubRequestHandler.getId()).thenReturn(STUB_HANDLER_ID);
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
    public void putStubDefersToFactory() throws ScriptException, URISyntaxException {
        configurationResource.postStub(LOCAL_PATH, JSON, mockRequest);

        verify(mockStubFactory).createStubFor(LOCAL_URI, JSON);
    }

    @Test
    public void putStubDefersToRouter() throws URISyntaxException, ScriptException {
        configurationResource.postStub(LOCAL_PATH, JSON, mockRequest);

        verify(mockRouter).addStub(LOCAL_URI, mockStubRequestHandler);
    }

    @Test
    public void putStubAddsQueryStringIfProvided() throws URISyntaxException, ScriptException {
        when(mockRequest.getQueryString()).thenReturn("key=val");

        configurationResource.postStub(LOCAL_PATH, JSON, mockRequest);

        verify(mockRouter).addStub(new URI(LOCAL_PATH + "?" + "key=val"), mockStubRequestHandler);
    }

    @Test
    public void putStubReturnsRouterIDForStub() throws URISyntaxException, ScriptException {

        assertThat((Integer) configurationResource.postStub(LOCAL_PATH, JSON, mockRequest).get("id"),
                equalTo(STUB_HANDLER_ID));
    }

    @Test
    public void deleteStubRemovesStubAtURI() throws ScriptException, URISyntaxException {
        configurationResource.postStub(LOCAL_PATH, JSON, mockRequest);

        configurationResource.deleteStub(LOCAL_URI, STUB_HANDLER_ID);

        verify(mockRouter).removeStub(LOCAL_URI, STUB_HANDLER_ID);
    }

    @Test
    public void getConfigDefersToRouter() {
        configurationResource.getConfiguration();

        verify(mockRouter).getUriHandlers();
    }

    @Test
    public void getConfigTranslatesShuntsToStrings() {
        ShuntRequestHandler mockShuntHandler = mock(ShuntRequestHandler.class);
        when(mockRouter.getUriHandlers()).thenReturn(
                Maps.asMap(LOCAL_PATH, Maps.<String, Object>asMap(RequestRouter.SHUNT_NODE, mockShuntHandler)));

        Map<String, Map<String, Object>> configuration = configurationResource.getConfiguration();

        assertThat((String) configuration.get(LOCAL_PATH).get("shunt"), equalTo(mockShuntHandler.toString()));
    }
}
