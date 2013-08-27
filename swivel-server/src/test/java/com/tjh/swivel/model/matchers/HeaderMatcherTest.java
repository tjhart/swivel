package com.tjh.swivel.model.matchers;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HeaderMatcherTest {
    @Test
    public void hasHeaderMatches(){
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("X-Forwarded-For")).thenReturn("198.124.3.3, 127.0.0.1");

        assertThat(mockRequest, HeaderMatcher.hasHeader("X-Forwarded-For", hasItem("127.0.0.1")));
    }
}
