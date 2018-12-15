package com.tjh.swivel.controller;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JerseyResponseFactoryTest {

    private JerseyResponseFactory responseFactory;
    private HttpResponse mockHttpResponse;

    @Before
    public void setUp() {
        responseFactory = new JerseyResponseFactory();

        mockHttpResponse = mock(HttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockHttpResponse.getAllHeaders()).thenReturn(new Header[]{new BasicHeader("name", "value")});
        when(mockStatusLine.getStatusCode()).thenReturn(Response.Status.OK.getStatusCode());
    }

    @Test
    public void responseHasExpectedStatus() throws IOException {

        Response response = responseFactory.createResponse(mockHttpResponse);

        assertThat(response.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
    }

    @Test
    public void responseHasExpectedHeaders() throws IOException {
        Response response = responseFactory.createResponse(mockHttpResponse);

        assertThat(response.getMetadata().getFirst("name"), equalTo("value"));
    }

    @Test
    public void createResponseAddsEntityIfAvailable() throws IOException {
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("entity"));

        Response response = responseFactory.createResponse(mockHttpResponse);

        assertThat(response.getEntity(), notNullValue());
    }
}
