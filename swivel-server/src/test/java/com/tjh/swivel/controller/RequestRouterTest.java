package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
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
    private HttpUriRequest mockUriRequest;

    @Before
    public void setUp() throws Exception {
        requestRouter = new RequestRouter();
        mockUriRequest = mock(HttpUriRequest.class);
        mockRequestHandler = mock(ShuntRequestHandler.class);

        when(mockUriRequest.getURI()).thenReturn(LOCAL_URI);
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

        requestRouter.work(mockUriRequest);

        verify(mockRequestHandler).handle(eq(mockUriRequest), any(HttpClient.class));
    }

    @Test
    public void workFindsHandlerOnShallowerPath() {
        requestRouter.setShunt(LOCAL_URI, mockRequestHandler);
        when(mockUriRequest.getURI()).thenReturn(DEEP_URI);

        requestRouter.work(mockUriRequest);

        verify(mockRequestHandler).handle(eq(mockUriRequest), any(HttpClient.class));
    }

    @Test(expected = RuntimeException.class)
    public void workThrowsOnUnknownURI() {
        requestRouter.work(mockUriRequest);
    }

    @Test
    public void workCleansUpOnUnknownURI() {
        try {
            requestRouter.work(mockUriRequest);
            fail("An exception should have been thrown");
        } catch (RuntimeException e) {
            assertThat(requestRouter.shuntPaths.isEmpty(), is(true));
        }
    }
}
