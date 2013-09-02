package com.tjh.swivel.controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProxyResourceTest {

    public static final String LOCAL_PATH = "local/path";
    public static final URI LOCAL_URI = URI.create(LOCAL_PATH);
    private ProxyResource proxyResource;
    private RequestRouter mockRouter;
    private HttpUriRequestFactory mockRequestFactory;
    private HttpServletRequest mockRequest;
    private HttpUriRequest mockUriRequest;
    private JerseyResponseFactory mockResponseFactory;

    @Before
    public void setUp() throws Exception {
        proxyResource = new ProxyResource();
        mockRouter = mock(RequestRouter.class);
        mockRequestFactory = mock(HttpUriRequestFactory.class);
        mockRequest = mock(HttpServletRequest.class);
        mockUriRequest = mock(HttpUriRequest.class);
        mockResponseFactory = mock(JerseyResponseFactory.class);

        proxyResource.setRouter(mockRouter);
        proxyResource.setRequestFactory(mockRequestFactory);
        proxyResource.setResponseFactory(mockResponseFactory);

        when(mockRequestFactory.createGetRequest(any(URI.class), any(HttpServletRequest.class)))
                .thenReturn(mockUriRequest);
    }

    @Test
    public void getDelegatesToRequestFactory() throws URISyntaxException, IOException {
        proxyResource.get(LOCAL_PATH, mockRequest);

        verify(mockRequestFactory).createGetRequest(LOCAL_URI, mockRequest);
    }

    @Test
    public void getDelegatesToRouter() throws URISyntaxException, IOException {
        proxyResource.get(LOCAL_PATH, mockRequest);

        verify(mockRouter).work(mockUriRequest);
    }

    @Test
    public void getDelegatesToResponseFactory() throws URISyntaxException, IOException {
        HttpResponse mockResponse = mock(HttpResponse.class);
        when(mockRouter.work(any(HttpUriRequest.class))).thenReturn(mockResponse);

        proxyResource.get(LOCAL_PATH, mockRequest);

        verify(mockResponseFactory).createResponse(mockResponse);
    }
}
