package com.tjh.swivel.model;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ShuntResponseHandlerTest {
    public static final URI SHUNT_URI = URI.create("http://someServer:1234");
    public static final URI RELATIVE_URI = URI.create("/some/path");
    public static final URI EXPECTED_URI = URI.create("http://someServer:1234/some/path");

    private ShuntResponseHandler shuntResponseHandler;
    private HttpClient mockClient;
    private HttpUriRequest mockRequest;

    @Before
    public void before() {
        mockClient = mock(HttpClient.class);
        mockRequest = mock(HttpUriRequest.class);

        shuntResponseHandler = new ShuntResponseHandler(SHUNT_URI, mockClient);
    }

    @Test
    public void constructionCapturesShuntURL() {
        assertThat(shuntResponseHandler.baseUri, sameInstance(SHUNT_URI));
    }

    @Test
    public void constructionCapturesHttpClient() {
        assertThat(shuntResponseHandler.client, sameInstance(mockClient));
    }

    @Test
    public void handleRequestDefersToConstructRequest() {
        ShuntResponseHandler shuntResponseHandlerSpy = spy(shuntResponseHandler);

        doReturn(mock(HttpUriRequest.class))
                .when(shuntResponseHandlerSpy)
                .createShuntRequest(any(HttpUriRequest.class));
        shuntResponseHandlerSpy.handle(mockRequest);

        verify(shuntResponseHandlerSpy).createShuntRequest(mockRequest);
    }

    @Test
    public void createShuntRequestWorksForGetRequest(){
        HttpGet get = new HttpGet(RELATIVE_URI);
        HttpUriRequest shuntRequest = shuntResponseHandler.createShuntRequest(get);

        assertThat(shuntRequest, instanceOf(HttpGet.class));
        assertThat(shuntRequest.getURI(), equalTo(EXPECTED_URI));
    }
}
