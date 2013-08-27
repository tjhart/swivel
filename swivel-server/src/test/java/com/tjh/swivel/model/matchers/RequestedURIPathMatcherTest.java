package com.tjh.swivel.model.matchers;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.tjh.swivel.model.matchers.RequestedURIPathMatcher.hasURIPath;

public class RequestedURIPathMatcherTest {
    @Test
    public void matchesExpectedPath() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("http://someServer/some/path");

        assertThat(mockRequest, hasURIPath(equalTo("/some/path")));
    }
}
