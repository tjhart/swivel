package com.tjh.swivel.controller;

import com.tjh.swivel.model.Configuration;
import com.tjh.swivel.model.RequestHandler;
import com.tjh.swivel.model.ResponseFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class RequestRouterTest {

    public static final URI LOCAL_URI = URI.create("some/path");
    public static final URI DEEP_URI = URI.create("some/path/deep");
    private RequestRouter requestRouter;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private HttpRequestBase mockRequestBase;
    @Mock
    private RequestHandler mockRequestHandler;
    @Mock
    private Configuration mockConfiguration;

    @Before
    public void setUp() {
        requestRouter = new RequestRouter();

        requestRouter.setResponseFactory(new ResponseFactory());
        requestRouter.setConfiguration(mockConfiguration);

        when(mockRequestBase.getURI()).thenReturn(LOCAL_URI);
        when(mockConfiguration.findRequestHandler(any(HttpRequestBase.class), anyString()))
                .thenReturn(mockRequestHandler);
    }

    @Test
    public void routeDelegatesToRequestHandler() {
        requestRouter.route(mockRequestBase);

        verify(mockRequestHandler).handle(eq(mockRequestBase), any(URI.class), any(HttpClient.class));
    }

    @Test
    public void routeFindsHandlerOnShallowerPath() {
        when(mockRequestBase.getURI()).thenReturn(DEEP_URI);
        when(mockConfiguration.findRequestHandler(mockRequestBase, DEEP_URI.toString()))
                .thenReturn(null);
        when(mockConfiguration.findRequestHandler(mockRequestBase, LOCAL_URI.toString()))
                .thenReturn(mockRequestHandler);

        requestRouter.route(mockRequestBase);

        verify(mockRequestHandler).handle(eq(mockRequestBase), any(URI.class), any(HttpClient.class));
    }

    @Test
    public void routeReturnsUnknownOnUnknownURI() {
        when(mockConfiguration.findRequestHandler(any(HttpRequestBase.class), anyString()))
                .thenReturn(null);
        HttpResponse route = requestRouter.route(mockRequestBase);

        assertThat(route.getStatusLine().getStatusCode(), equalTo(404));
    }

    @Test
    public void routeRemovesMatchedPathFromURISentToHandler() {
        when(mockRequestBase.getURI()).thenReturn(DEEP_URI);
        when(mockConfiguration.findRequestHandler(mockRequestBase, DEEP_URI.toString()))
                .thenReturn(null);
        when(mockConfiguration.findRequestHandler(mockRequestBase, LOCAL_URI.toString()))
                .thenReturn(mockRequestHandler);

        requestRouter.route(mockRequestBase);

        verify(mockRequestHandler).handle(eq(mockRequestBase), eq(LOCAL_URI), any(HttpClient.class));
    }
}
