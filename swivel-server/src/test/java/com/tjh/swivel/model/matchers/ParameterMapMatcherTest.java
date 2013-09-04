package com.tjh.swivel.model.matchers;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;
import vanderbilt.util.Maps;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParameterMapMatcherTest {

    @Test
    public void queryParamsMatch() {
        HttpUriRequest mockRequest = mock(HttpUriRequest.class);

        when(mockRequest.getURI()).thenReturn(URI.create("some/path?key=value"));

        Map<String, List<String>> copy = Maps.asMap("key", Arrays.asList("value"));
        assertThat(mockRequest, ParameterMapMatcher.hasParameterMap(equalTo(copy)));
    }
}
