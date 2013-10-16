package com.tjh.swivel.controller;

import com.tjh.swivel.model.Configuration;
import com.tjh.swivel.model.StubRequestHandler;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import vanderbilt.util.Maps;

import javax.script.ScriptException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConfigureStubResourceTest {
    public static final String LOCAL_PATH = "extra/path";
    public static final URI LOCAL_URI = URI.create(LOCAL_PATH);
    public static final int STUB_HANDLER_ID = 123;
    private ConfigureStubResource configureStubResource;
    private Configuration mockConfiguration;
    private Map<String, Object> json = new HashMap<String, Object>();
    private StubRequestHandler mockStubRequestHandler;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        configureStubResource = new ConfigureStubResource();
        mockConfiguration = mock(Configuration.class);
        mockStubRequestHandler = mock(StubRequestHandler.class);

        configureStubResource.setConfiguration(mockConfiguration);

        configureStubResource = spy(configureStubResource);

        doReturn(mockStubRequestHandler)
                .when(configureStubResource)
                .createStub(anyMap());
        when(mockStubRequestHandler.getId()).thenReturn(STUB_HANDLER_ID);
    }

    @Test
    public void postStubDefersToFactory() throws ScriptException, URISyntaxException {
        configureStubResource.postStub(LOCAL_PATH, json);

        verify(configureStubResource).createStub(json);
    }

    @Test
    public void postStubDefersToConfiguration() throws URISyntaxException, ScriptException {
        configureStubResource.postStub(LOCAL_PATH, json);

        verify(mockConfiguration).addStub(LOCAL_URI, mockStubRequestHandler);
    }

    @Test
    public void postStubReturnsRouterIDForStub() throws URISyntaxException, ScriptException {

        assertThat((Integer) configureStubResource.postStub(LOCAL_PATH, json).get("id"),
                equalTo(STUB_HANDLER_ID));
    }

    @Test
    public void deleteStubRemovesStubAtURI() throws ScriptException, URISyntaxException {
        configureStubResource.postStub(LOCAL_PATH, json);

        configureStubResource.deleteStub(LOCAL_URI, STUB_HANDLER_ID);

        verify(mockConfiguration).removeStub(LOCAL_URI, STUB_HANDLER_ID);
    }

    @Test
    public void getStubDefersToConfiguration() {
        List<Integer> stubIds = Arrays.asList(1);
        configureStubResource.getStub(LOCAL_PATH, stubIds);

        verify(mockConfiguration).getStubs(LOCAL_PATH, stubIds);
    }

    @Test
    public void getStubsCollectsMapsReturnedByConfiguration() {
        Map<String, Object> expectedMap = new HashMap<String, Object>();

        when(mockConfiguration.getStubs(eq(LOCAL_PATH), anyList()))
                .thenReturn(Arrays.asList(mockStubRequestHandler));
        when(mockStubRequestHandler.toMap()).thenReturn(expectedMap);
        assertThat(configureStubResource.getStub(LOCAL_PATH, Collections.<Integer>emptyList()),
                CoreMatchers.<Collection<Map<String, Object>>>equalTo(Arrays.asList(expectedMap)));
    }

    @Test
    public void editStubDefersToCreateStub() throws ScriptException, URISyntaxException {
        configureStubResource.editStub(LOCAL_PATH + "/12345", json);

        verify(configureStubResource).createStub(json);
    }

    @Test
    public void editStubDefersToConfigurationAsExpected() throws ScriptException, URISyntaxException {
        configureStubResource.editStub(LOCAL_PATH + "/12345", json);

        verify(mockConfiguration).replaceStub(URI.create(LOCAL_PATH), 12345, mockStubRequestHandler);
    }

    @Test
    public void editStubReturnsIDMap() throws ScriptException, URISyntaxException {
        assertThat(configureStubResource.editStub(LOCAL_PATH + "/12345", json),
                CoreMatchers.<Map<String, Object>>equalTo(
                        Maps.<String, Object>asMap(ConfigureStubResource.STUB_ID_KEY, STUB_HANDLER_ID)));
    }
}
