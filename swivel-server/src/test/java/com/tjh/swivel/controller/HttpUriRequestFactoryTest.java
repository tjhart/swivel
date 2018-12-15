package com.tjh.swivel.controller;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpUriRequestFactoryTest {

    public static final URI LOCAL_URI = URI.create("some/path");
    public static final String HEADER_NAME = "headerName";
    public static final String HEADER = "header";
    public static final String REMOTE_ADDR = "127.0.0.1";

    private HttpUriRequestFactory httpUriRequestFactory;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private HttpServletRequest mockServletRequest;
    @Mock
    private Enumeration<String> mockEnumeration;
    @Mock
    private HttpUriRequest mockUriRequest;

    @Before
    public void setUp() {
        httpUriRequestFactory = new HttpUriRequestFactory();

        when(mockServletRequest.getHeaderNames()).thenReturn(mockEnumeration);
        when(mockServletRequest.getParameterMap()).thenReturn(Collections.emptyMap());
        when(mockServletRequest.getRemoteAddr()).thenReturn(REMOTE_ADDR);
    }

    @Test
    public void createGetRequestSetsURI() {
        assertThat(httpUriRequestFactory.createGetRequest(LOCAL_URI, mockServletRequest).getURI(), equalTo(LOCAL_URI));
    }

    @Test
    public void createGetRequestUsesQueryStringFromRequest() {
        when(mockServletRequest.getQueryString()).thenReturn("key=val");

        HttpRequestBase getRequest =
                httpUriRequestFactory.createGetRequest(URI.create("some/path"), mockServletRequest);
        assertThat(getRequest.getURI().getQuery(), equalTo("key=val"));
    }

    @Test
    public void createGetRequestDefersToPopulateRequest() {
        HttpUriRequestFactory spy = spy(httpUriRequestFactory);

        spy.createGetRequest(LOCAL_URI, mockServletRequest);

        verify(spy).populateRequest(any(HttpUriRequest.class), eq(mockServletRequest));
    }

    @Test
    public void populateRequestPopulatesHeaders() {
        when(mockEnumeration.hasMoreElements()).thenReturn(true, false);
        when(mockEnumeration.nextElement()).thenReturn(HEADER_NAME);
        when(mockServletRequest.getHeader(HEADER_NAME)).thenReturn(HEADER);

        httpUriRequestFactory.populateRequest(mockUriRequest, mockServletRequest);

        InOrder inOrder = inOrder(mockUriRequest, mockServletRequest, mockEnumeration);
        inOrder.verify(mockServletRequest).getHeaderNames();
        inOrder.verify(mockEnumeration).hasMoreElements();
        inOrder.verify(mockEnumeration).nextElement();
        inOrder.verify(mockServletRequest).getHeader(HEADER_NAME);
        inOrder.verify(mockUriRequest).addHeader(HEADER_NAME, HEADER);
    }

    @Test
    public void populateRequestAddsRemoteHostToXForwardedFor() {
        HttpGet uriRequest = new HttpGet();
        httpUriRequestFactory.populateRequest(uriRequest, mockServletRequest);

        assertThat(uriRequest.getHeaders(HttpUriRequestFactory.X_FORWARDED_FOR_HEADER)[0].getValue(),
                equalTo(REMOTE_ADDR));
    }
}
