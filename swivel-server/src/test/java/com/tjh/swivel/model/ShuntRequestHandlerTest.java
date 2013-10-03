package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShuntRequestHandlerTest {
    public static final URL SHUNT_URL;
    public static final URI RELATIVE_URI = URI.create("/some/path/deep");
    public static final URI EXPECTED_URI = URI.create("http://someServer:1234/deep");
    public static final URI MATCHED_URI = URI.create("/some/path");

    static {
        try {
            SHUNT_URL = new URL("http://someServer:1234");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private ShuntRequestHandler shuntRequestHandler;
    private HttpClient mockClient;
    private HttpRequestBase mockRequest;

    @Before
    public void before() throws IOException {
        mockClient = mock(HttpClient.class);
        mockRequest = mock(HttpRequestBase.class);
        HttpResponse mockResponse = mock(HttpResponse.class);

        when(mockRequest.getURI()).thenReturn(RELATIVE_URI);
        when(mockClient.execute(any(HttpUriRequest.class))).thenReturn(mockResponse);

        shuntRequestHandler = new ShuntRequestHandler(SHUNT_URL);
    }

    @Test
    public void constructionCapturesShuntURL() {
        assertThat(shuntRequestHandler.remoteURL, sameInstance(SHUNT_URL));
    }

    @Test
    public void handleRequestDefersToConstructRequest() {
        ShuntRequestHandler shuntResponseHandlerSpy = spy(shuntRequestHandler);

        doReturn(mock(HttpUriRequest.class))
                .when(shuntResponseHandlerSpy)
                .createShuntRequest(any(HttpRequestBase.class), any(URI.class));

        shuntResponseHandlerSpy.handle(mockRequest, MATCHED_URI, mockClient);

        verify(shuntResponseHandlerSpy).createShuntRequest(mockRequest, MATCHED_URI);
    }

    @Test
    public void handleRequestDefersToClient() throws IOException {
        shuntRequestHandler.handle(mockRequest, MATCHED_URI, mockClient);

        verify(mockClient).execute(mockRequest);
    }

    @Test
    public void createShuntRequestSetsExpectedURI() {

        shuntRequestHandler.createShuntRequest(mockRequest, MATCHED_URI);

        verify(mockRequest).setURI(EXPECTED_URI);
    }
}
