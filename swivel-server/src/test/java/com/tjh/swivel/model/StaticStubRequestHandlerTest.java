package com.tjh.swivel.model;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;
import vanderbilt.util.Maps;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class StaticStubRequestHandlerTest {

    private StaticStubRequestHandler staticResponseHandler;

    @Before
    public void before() {
        staticResponseHandler = new StaticStubRequestHandler(Maps.<String, Object>asMap(
                AbstractStubRequestHandler.DESCRIPTION_KEY, "description",
                AbstractStubRequestHandler.WHEN_KEY, Maps.asMap(WhenMatcher.METHOD_KEY, "GET"),

                AbstractStubRequestHandler.THEN_KEY,
                Maps.asMap(ResponseFactory.STATUS_CODE_KEY, 200)
        ));
    }

    @Test
    public void handleReturnsResponse() {
        assertThat(staticResponseHandler.handle(mock(HttpUriRequest.class), null, null), notNullValue());
    }
}
