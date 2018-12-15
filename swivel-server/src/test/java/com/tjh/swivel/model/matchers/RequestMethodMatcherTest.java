package com.tjh.swivel.model.matchers;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static com.tjh.swivel.model.matchers.RequestMethodMatcher.hasMethod;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class RequestMethodMatcherTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private HttpUriRequest mockRequest;

    @Test
    public void hasMethodMatches(){
        when(mockRequest.getMethod()).thenReturn("foo");
        assertThat(mockRequest, hasMethod(equalTo("foo")));
    }
}
