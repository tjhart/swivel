package com.tjh.swivel.model.matchers;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;

import java.net.URI;

import static com.tjh.swivel.model.matchers.RequestedURIPathMatcher.hasURIPath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestedURIPathMatcherTest {
    @Test
    public void matchesExpectedPath() {
        HttpUriRequest mockRequest = mock(HttpUriRequest.class);
        when(mockRequest.getURI()).thenReturn(URI.create("http://someServer/some/path"));

        assertThat(mockRequest, hasURIPath(equalTo("/some/path")));
    }
}
