package com.tjh.swivel.model.matchers;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;

import static com.tjh.swivel.model.matchers.RequestMethodMatcher.hasMethod;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestMethodMatcherTest {

    @Test
    public void hasMethodMatches(){
        HttpUriRequest mockRequest = mock(HttpUriRequest.class);
        when(mockRequest.getMethod()).thenReturn("foo");
        assertThat(mockRequest, hasMethod(equalTo("foo")));
    }
}
