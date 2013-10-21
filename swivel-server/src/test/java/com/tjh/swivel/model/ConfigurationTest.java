package com.tjh.swivel.model;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConfigurationTest {
    public static final URI LOCAL_URI = URI.create("some/path");
    public static final int STUB_HANDLER_ID = 456;
    private ShuntRequestHandler mockShuntHandler;
    private Configuration configuration;
    private StubRequestHandler mockStubHandler;
    private HttpRequestBase mockRequest;

    @Before
    public void setUp() throws Exception {
        configuration = new Configuration();

        mockShuntHandler = mock(ShuntRequestHandler.class);
        mockStubHandler = mock(StubRequestHandler.class);
        mockRequest = mock(HttpRequestBase.class);

        when(mockStubHandler.getId()).thenReturn(STUB_HANDLER_ID);
        when(mockStubHandler.matches(any(HttpUriRequest.class))).thenReturn(true);
    }

    @Test
    public void setShuntPutsRequestHandlerAtExpectedPath() {
        configuration.setShunt(LOCAL_URI, mockShuntHandler);

        assertThat((ShuntRequestHandler) configuration.uriHandlers
                .get(LOCAL_URI.getPath())
                .get(Configuration.SHUNT_NODE),
                equalTo(mockShuntHandler));
    }

    @Test
    public void deleteShuntRemovesHandler() {
        configuration.setShunt(LOCAL_URI, mockShuntHandler);

        configuration.deleteShunt(LOCAL_URI);

        assertThat(configuration.uriHandlers.get(LOCAL_URI.toString()).containsKey(Configuration.SHUNT_NODE),
                is(false));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addStubPutsStubInListAtPath() {
        configuration.addStub(LOCAL_URI, mockStubHandler);

        assertThat(((List<StubRequestHandler>) configuration.uriHandlers
                .get(LOCAL_URI.getPath())
                .get(Configuration.STUB_NODE)).get(0),
                sameInstance(mockStubHandler));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void removeStubRemovesStubInListAtPath() {
        configuration.addStub(LOCAL_URI, mockStubHandler);

        configuration.removeStub(LOCAL_URI, STUB_HANDLER_ID);

        assertThat((List<StubRequestHandler>) configuration.uriHandlers
                .get(LOCAL_URI.getPath())
                .get(Configuration.STUB_NODE),
                not(hasItem(mockStubHandler)));
    }

    @Test
    public void findRequestHandlerFindsStub() {
        configuration.addStub(LOCAL_URI, mockStubHandler);

        assertThat(configuration.findRequestHandler(mockRequest, LOCAL_URI.getPath()),
                equalTo((RequestHandler) mockStubHandler));
    }

    @Test
    public void findRequestHandlerFindsShunt() {
        configuration.setShunt(LOCAL_URI, mockShuntHandler);

        assertThat(configuration.findRequestHandler(mockRequest, LOCAL_URI.getPath()),
                equalTo((RequestHandler) mockShuntHandler));
    }

    @Test
    public void findRequestHandlerPrefersStub() {
        configuration.addStub(LOCAL_URI, mockStubHandler);
        configuration.setShunt(LOCAL_URI, mockShuntHandler);

        assertThat(configuration.findRequestHandler(mockRequest, LOCAL_URI.getPath()),
                equalTo((RequestHandler) mockStubHandler));
    }

    @Test
    public void findRequestHandlerReturnsNull() {
        assertThat(configuration.findRequestHandler(mockRequest, LOCAL_URI.getPath()), nullValue());
    }

    @Test
    public void getStubsReturnsAllIfStubIdsIsEmpty() {
        configuration.addStub(LOCAL_URI, mockStubHandler);

        assertThat(configuration.getStubs(LOCAL_URI.getPath(), Collections.<Integer>emptyList()),
                hasItem(mockStubHandler));
    }

    @Test
    public void getStubsOnlyReturnsMatchingStubs() {
        StubRequestHandler mockNonMatchingHandler = mock(StubRequestHandler.class);
        configuration.addStub(LOCAL_URI, mockStubHandler);
        configuration.addStub(LOCAL_URI, mockNonMatchingHandler);

        assertThat(configuration.getStubs(LOCAL_URI.getPath(), Arrays.asList(STUB_HANDLER_ID)),
                equalTo((Collection) Arrays.asList(mockStubHandler)));
    }

    @Test
    public void replaceStubBehaves() {
        StubRequestHandler mockNewStub = mock(StubRequestHandler.class);
        configuration.addStub(LOCAL_URI, mockStubHandler);
        configuration.replaceStub(LOCAL_URI, STUB_HANDLER_ID, mockNewStub);

        assertThat(configuration.getStubs(LOCAL_URI.getPath(), Collections.<Integer>emptyList()),
                equalTo((Collection) Arrays.asList(mockNewStub)));
    }

    @Test
    public void cleanRemovesNodeType() {
        Map<String, Object> mockMap = mock(Map.class);
        configuration.clean("some/path", mockMap, Configuration.SHUNT_NODE);

        verify(mockMap).remove(Configuration.SHUNT_NODE);
    }

    @Test
    public void cleanRemovesStubNodeIfEmpty() {
        Map<String, Object> mockMap = mock(Map.class);
        when(mockMap.containsKey(Configuration.STUB_NODE))
                .thenReturn(true);
        when(mockMap.get(Configuration.STUB_NODE))
                .thenReturn(Collections.emptyList());

        configuration.clean("some/path", mockMap, Configuration.SHUNT_NODE);
        verify(mockMap).remove(Configuration.STUB_NODE);
    }
}
