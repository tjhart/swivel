package com.tjh.swivel.model.matchers;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.net.URI;

import static com.tjh.swivel.model.matchers.RequestedURIPathMatcher.hasURIPath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class RequestedURIPathMatcherTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private HttpUriRequest mockRequest;

    @Test
    public void matchesExpectedPath() {
        when(mockRequest.getURI()).thenReturn(URI.create("http://someServer/some/path"));

        assertThat(mockRequest, hasURIPath(equalTo("/some/path")));
    }
}
