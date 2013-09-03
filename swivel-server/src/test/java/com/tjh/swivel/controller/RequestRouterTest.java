package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;
import com.tjh.swivel.model.StubFactory;
import com.tjh.swivel.model.StubRequestHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.junit.Test;
import vanderbilt.util.Maps;

import javax.script.ScriptException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RequestRouterTest {

    public static final URI LOCAL_URI = URI.create("some/path");
    public static final URI DEEP_URI = URI.create("some/path/deep");
    public static final Map<String, Object> SUB_DESCRIPTION = Collections.emptyMap();
    private ShuntRequestHandler mockRequestHandler;
    private RequestRouter requestRouter;
    private HttpRequestBase mockRequestBase;
    private StubFactory mockStubFactory;
    private StubRequestHandler mockStubHandler;

    @Before
    public void setUp() throws Exception {
        requestRouter = new RequestRouter();
        mockRequestBase = mock(HttpRequestBase.class);
        mockRequestHandler = mock(ShuntRequestHandler.class);
        mockStubFactory = mock(StubFactory.class);
        mockStubHandler = mock(StubRequestHandler.class);

        requestRouter.setStubFactory(mockStubFactory);

        when(mockRequestBase.getURI()).thenReturn(LOCAL_URI);
        when(mockStubFactory.createStubFor(any(URI.class), any(Map.class))).thenReturn(mockStubHandler);
    }

    @Test
    public void setShuntPutsRequestHandlerAtExpectedPath() {
        requestRouter.setShunt(LOCAL_URI, mockRequestHandler);

        assertThat((ShuntRequestHandler) Maps.valueFor(requestRouter.shuntPaths, LOCAL_URI.getPath().split("/")),
                equalTo(mockRequestHandler));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setShuntThrowsIfItemExistsAtPath() {
        requestRouter.setShunt(DEEP_URI, mockRequestHandler);

        requestRouter.setShunt(LOCAL_URI, mockRequestHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setShuntThrowsIfLeafEncounteredOnWayToPath() {
        requestRouter.setShunt(LOCAL_URI, mockRequestHandler);

        requestRouter.setShunt(DEEP_URI, mockRequestHandler);
    }

    @Test
    public void deleteShuntRemovesHandler() {
        requestRouter.setShunt(LOCAL_URI, mockRequestHandler);
        requestRouter.deleteShunt(LOCAL_URI);

        assertThat(requestRouter.shuntPaths.keySet().isEmpty(), is(true));
    }

    @Test
    public void workDelegatesToShunt() {
        requestRouter.setShunt(LOCAL_URI, mockRequestHandler);

        requestRouter.work(mockRequestBase);

        verify(mockRequestHandler).handle(eq(mockRequestBase), any(URI.class), any(HttpClient.class));
    }

    @Test
    public void workFindsHandlerOnShallowerPath() {
        requestRouter.setShunt(LOCAL_URI, mockRequestHandler);
        when(mockRequestBase.getURI()).thenReturn(DEEP_URI);

        requestRouter.work(mockRequestBase);

        verify(mockRequestHandler).handle(eq(mockRequestBase), any(URI.class), any(HttpClient.class));
    }

    @Test(expected = RuntimeException.class)
    public void workThrowsOnUnknownURI() {
        requestRouter.work(mockRequestBase);
    }

    @Test
    public void workCleansUpOnUnknownURI() {
        try {
            requestRouter.work(mockRequestBase);
            fail("An exception should have been thrown");
        } catch (RuntimeException e) {
            assertThat(requestRouter.shuntPaths.isEmpty(), is(true));
        }
    }

    @Test
    public void workRemovesMatchedPathFromURISentToHandler() {
        requestRouter.setShunt(LOCAL_URI, mockRequestHandler);
        when(mockRequestBase.getURI()).thenReturn(DEEP_URI);

        requestRouter.work(mockRequestBase);

        verify(mockRequestHandler).handle(eq(mockRequestBase), eq(LOCAL_URI), any(HttpClient.class));
    }

    @Test
    public void addStubDefersToStubFactory() throws ScriptException {
        requestRouter.addStub(LOCAL_URI, SUB_DESCRIPTION);

        verify(mockStubFactory).createStubFor(LOCAL_URI, SUB_DESCRIPTION);
    }

    @Test
    public void addStubPutsStubInListAtPath() throws ScriptException {
        requestRouter.addStub(LOCAL_URI, SUB_DESCRIPTION);

        Map<String, Object> node = Maps.valueFor(requestRouter.stubPaths, LOCAL_URI.getPath().split("/"));
        assertThat(node.get(RequestRouter.LEAF_KEY), instanceOf(List.class));
    }
}
