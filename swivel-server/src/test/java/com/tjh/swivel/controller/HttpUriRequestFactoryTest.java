package com.tjh.swivel.controller;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.HttpParams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import vanderbilt.util.Maps;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpUriRequestFactoryTest {

    public static final URI LOCAL_URI = URI.create("some/path");
    public static final String HEADER_NAME = "headerName";
    public static final String HEADER = "header";
    public static final String PARAM_KEY = "key";
    public static final String PARAM_VAL1 = "val1";
    public static final String PARAM_VAL2 = "val2";

    private HttpUriRequestFactory httpUriRequestFactory;
    private HttpServletRequest mockServletRequest;
    private Enumeration mockEnumeration;
    private HttpUriRequest mockUriRequest;

    @Before
    public void setUp() throws Exception {
        httpUriRequestFactory = new HttpUriRequestFactory();
        mockServletRequest = mock(HttpServletRequest.class);
        mockUriRequest = mock(HttpUriRequest.class);
        mockEnumeration = mock(Enumeration.class);

        when(mockServletRequest.getHeaderNames()).thenReturn(mockEnumeration);
        when(mockServletRequest.getParameterMap()).thenReturn(Collections.emptyMap());
    }

    @Test
    public void createGetRequestSetsURI() {

        assertThat(httpUriRequestFactory.createGetRequest(LOCAL_URI, mockServletRequest).getURI(), equalTo(LOCAL_URI));
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
    public void populateRequestPopulatesParameters() {
        String[] params = {PARAM_VAL1, PARAM_VAL2};
        when(mockServletRequest.getParameterMap()).thenReturn(
                Maps.asMap(PARAM_KEY, params));

        httpUriRequestFactory.populateRequest(mockUriRequest, mockServletRequest);

        InOrder inOrder = inOrder(mockUriRequest, mockServletRequest);
        ArgumentCaptor<HttpParams> paramsCaptor = ArgumentCaptor.forClass(HttpParams.class);

        inOrder.verify(mockServletRequest).getParameterMap();
        inOrder.verify(mockUriRequest).setParams(paramsCaptor.capture());

        HttpParams httpParams = paramsCaptor.getValue();
        assertThat((String[]) httpParams.getParameter(PARAM_KEY), equalTo(params));
    }
}
