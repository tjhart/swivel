package com.tjh.swivel.model.matchers;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.tjh.swivel.model.matchers.RequestMethodMatcher.hasMethod;

public class RequestMethodMatcherTest {

    @Test
    public void hasMethodMatches(){
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getMethod()).thenReturn("foo");
        assertThat(mockRequest, hasMethod(equalTo("foo")));
    }
}
