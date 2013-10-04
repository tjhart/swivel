package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class StaticStubRequestHandlerTest {

    private StaticStubRequestHandler staticResponseHandler;
    private HttpResponse mockHttpResponse;

    @Before
    public void before() {
        mockHttpResponse = mock(HttpResponse.class);
        WhenMatcher mockMatcher = mock(WhenMatcher.class);
        Map<String, Object> then = mock(Map.class);
        staticResponseHandler = new StaticStubRequestHandler("description", mockMatcher, mockHttpResponse, then);
    }

    @Test
    public void constructorCapturesResponse() {
        assertThat(staticResponseHandler.httpResponse, sameInstance(mockHttpResponse));
    }

    @Test
    public void handleReturnsResponse() {
        assertThat(staticResponseHandler.handle(mock(HttpUriRequest.class), null, null),
                sameInstance(mockHttpResponse));
    }
}
