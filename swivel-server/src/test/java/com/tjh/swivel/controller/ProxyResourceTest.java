package com.tjh.swivel.controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProxyResourceTest {

    public static final URI LOCAL_URI = URI.create("local/path");
    private ProxyResource proxyResource;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private RequestRouter mockRouter;
    @Mock
    private HttpUriRequestFactory mockRequestFactory;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpRequestBase mockRequestBase;
    @Mock
    private JerseyResponseFactory mockResponseFactory;
    @Mock
    private HttpResponse mockResponse;

    @Before
    public void setUp() {
        proxyResource = new ProxyResource();

        proxyResource.setRouter(mockRouter);
        proxyResource.setRequestFactory(mockRequestFactory);
        proxyResource.setResponseFactory(mockResponseFactory);

        when(mockRequestFactory.createGetRequest(any(URI.class), any(HttpServletRequest.class)))
                .thenReturn(mockRequestBase);
    }

    @Test
    public void getDelegatesToRequestFactory() throws IOException {
        proxyResource.get(LOCAL_URI, mockRequest);

        verify(mockRequestFactory).createGetRequest(LOCAL_URI, mockRequest);
    }

    @Test
    public void getDelegatesToRouter() throws IOException {
        proxyResource.get(LOCAL_URI, mockRequest);

        verify(mockRouter).route(mockRequestBase);
    }

    @Test
    public void getDelegatesToResponseFactory() throws IOException {
        when(mockRouter.route(any(HttpRequestBase.class))).thenReturn(mockResponse);

        proxyResource.get(LOCAL_URI, mockRequest);

        verify(mockResponseFactory).createResponse(mockResponse);
    }
}
