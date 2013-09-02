package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.junit.Test;
import vanderbilt.util.Maps;

import java.net.URI;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.equalTo;
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
    private ShuntRequestHandler mockRequestHandler;
    private RequestRouter requestRouter;
    private HttpRequestBase mockRequestBase;

    @Before
    public void setUp() throws Exception {
        requestRouter = new RequestRouter();
        mockRequestBase = mock(HttpRequestBase.class);
        mockRequestHandler = mock(ShuntRequestHandler.class);

        when(mockRequestBase.getURI()).thenReturn(LOCAL_URI);
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
}
