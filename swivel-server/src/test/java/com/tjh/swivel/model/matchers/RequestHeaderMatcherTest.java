package com.tjh.swivel.model.matchers;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestHeaderMatcherTest {
    @Test
    public void hasHeaderMatches() {
        HttpUriRequest mockRequest = mock(HttpUriRequest.class);
        when(mockRequest.getHeaders("X-Forwarded-For"))
                .thenReturn(new Header[]{new BasicHeader("X-Forwarded-For", "198.124.3.3, 127.0.0.1")});

        assertThat(mockRequest,
                RequestHeaderMatcher.hasHeader("X-Forwarded-For", hasItem(containsString("127.0.0.1"))));
    }
}
