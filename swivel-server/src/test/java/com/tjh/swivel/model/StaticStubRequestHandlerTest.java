package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class StaticStubRequestHandlerTest {

    private StaticStubRequestHandler staticResponseHandler;
    private HttpResponse mockHttpResponse;

    @Before
    public void before(){
        mockHttpResponse = mock(HttpResponse.class);
        staticResponseHandler = new StaticStubRequestHandler(mockHttpResponse);
    }

    @Test
    public void constructorCapturesResponse(){
        assertThat(staticResponseHandler.httpResponse, sameInstance(mockHttpResponse));
    }

    @Test
    public void handleReturnsResponse(){
        assertThat(staticResponseHandler.handle(mock(HttpUriRequest.class)), sameInstance(mockHttpResponse));
    }
}
