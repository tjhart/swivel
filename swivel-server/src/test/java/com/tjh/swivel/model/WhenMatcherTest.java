package com.tjh.swivel.model;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.Test;
import vanderbilt.util.Maps;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class WhenMatcherTest {
    public static final Map<String, String> WHEN_MAP = Maps.asMap(WhenMatcher.METHOD_KEY, "POST");
    public static final String APPLICATION_JSON = ContentType.APPLICATION_JSON.toString();
    public static final String CONTENT = "fred";
    public static final StringEntity STRING_ENTITY = new StringEntity(CONTENT, ContentType.APPLICATION_JSON);

    private HttpEntityEnclosingRequestBase mockEnclosingRequest;

    @Before
    public void setUp() throws IOException {
        mockEnclosingRequest = mock(HttpEntityEnclosingRequestBase.class);

        when(mockEnclosingRequest.getURI()).thenReturn(URI.create("some/path"));
        when(mockEnclosingRequest.getMethod()).thenReturn("POST");
        when(mockEnclosingRequest.getEntity()).thenReturn(STRING_ENTITY);
    }

    @Test
    public void matchesMethod() throws IOException {
        //This is backwards from normal - expected is usually on the left.
        //but if I put the actual on the left, when the assertion fails, I get a nice
        //description of what's wrong
        assertThat(mockEnclosingRequest, new WhenMatcher(WHEN_MAP));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfContentTypeProvidedAndDoesNotMatch() throws IOException {
        when(mockEnclosingRequest.getEntity()).thenReturn(new StringEntity("foo"));
        assertThat(mockEnclosingRequest,
                new WhenMatcher(Maps.merge(WHEN_MAP, Maps.asMap(WhenMatcher.CONTENT_TYPE_KEY, APPLICATION_JSON))));
    }

    @Test
    public void matchesIfContentTypeProvidedAndMatches() throws IOException {
        assertThat(mockEnclosingRequest,
                new WhenMatcher(Maps.merge(WHEN_MAP, Maps.asMap(WhenMatcher.CONTENT_TYPE_KEY, APPLICATION_JSON))));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfContentProvidedAndDoesNotMatch() throws IOException {
        assertThat(mockEnclosingRequest,
                new WhenMatcher(Maps.merge(WHEN_MAP, Maps.asMap(WhenMatcher.CONTENT_KEY, "Yummy!"))));
    }

    @Test
    public void matchesIfContentProvidedAndMatches() throws IOException {
        assertThat(mockEnclosingRequest,
                new WhenMatcher(Maps.merge(WHEN_MAP, Maps.asMap(WhenMatcher.CONTENT_KEY, CONTENT))));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfQueryProvidedAndDoesNotMatch() throws IOException {
        assertThat(mockEnclosingRequest,
                new WhenMatcher(Maps.merge(WHEN_MAP, Maps.asMap(WhenMatcher.QUERY_KEY, "key=val"))));
    }

    @Test
    public void matchesIfQueryProvidedAndRequestContainsQuery() throws IOException {
        when(mockEnclosingRequest.getURI()).thenReturn(URI.create("some/path?key=val"));

        assertThat(mockEnclosingRequest,
                new WhenMatcher(Maps.merge(WHEN_MAP, Maps.asMap(WhenMatcher.QUERY_KEY, "key=val"))));
    }

    @Test
    public void matchesIfRemoteAddrInXForwardedForHeader() throws IOException {
        when(mockEnclosingRequest.getHeaders("X-Forwarded-For")).thenReturn(
                new Header[]{new BasicHeader("X-Forwarded-For", "198.154.20.2, 127.0.0.1")});

        assertThat(mockEnclosingRequest,
                new WhenMatcher(Maps.merge(WHEN_MAP, Maps.asMap(WhenMatcher.REMOTE_ADDR_KEY, "127.0.0.1"))));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfScriptProvidedAndDoesNotMatch() {
        when(mockEnclosingRequest.getMethod()).thenReturn("GET");

        assertThat(mockEnclosingRequest, new WhenMatcher(Maps.merge(WHEN_MAP,
                Maps.asMap(WhenMatcher.SCRIPT_KEY, "(function(){return request.getMethod() == 'POST';})();"))));
    }

    @Test
    public void matchesIfScriptMatches() {
        when(mockEnclosingRequest.getURI()).thenReturn(URI.create("?key=val"));

        assertThat(mockEnclosingRequest, new WhenMatcher(Maps.merge(WHEN_MAP,
                Maps.asMap(WhenMatcher.SCRIPT_KEY, "(function(){return request.getURI().getQuery() == 'key=val';})();"))));
    }
}
