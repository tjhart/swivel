package com.tjh.swivel.model;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class WhenMatcherTest {
    public static final Map<String, String> WHEN_MAP = Map.of(WhenMatcher.METHOD_KEY, "POST");
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
        Map<String, String> result = new HashMap<>(WHEN_MAP);

        result.putAll(Map.of(WhenMatcher.CONTENT_TYPE_KEY, APPLICATION_JSON));

        assertThat(mockEnclosingRequest,
                new WhenMatcher(result));
    }

    @Test
    public void matchesIfContentTypeProvidedAndMatches() throws IOException {
        Map<String, String> result = new HashMap<>(WHEN_MAP);

        result.putAll(Map.of(WhenMatcher.CONTENT_TYPE_KEY, APPLICATION_JSON));

        assertThat(mockEnclosingRequest,
                new WhenMatcher(result));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfContentProvidedAndDoesNotMatch() throws IOException {
        Map<String, String> result = new HashMap<>(WHEN_MAP);

        result.putAll(Map.of(WhenMatcher.CONTENT_KEY, "Yummy!"));

        assertThat(mockEnclosingRequest,
                new WhenMatcher(result));
    }

    @Test
    public void matchesIfContentProvidedAndMatches() throws IOException {
        Map<String, String> result = new HashMap<>(WHEN_MAP);

        result.putAll(Map.of(WhenMatcher.CONTENT_KEY, CONTENT));

        assertThat(mockEnclosingRequest,
                new WhenMatcher(result));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfQueryProvidedAndDoesNotMatch() throws IOException {
        Map<String, String> result = new HashMap<>(WHEN_MAP);

        result.putAll(Map.of(WhenMatcher.QUERY_KEY, "key=val"));

        assertThat(mockEnclosingRequest,
                new WhenMatcher(result));
    }

    @Test
    public void matchesIfQueryProvidedAndRequestContainsQuery() throws IOException {
        when(mockEnclosingRequest.getURI()).thenReturn(URI.create("some/path?key=val"));

        Map<String, String> result = new HashMap<>(WHEN_MAP);

        result.putAll(Map.of(WhenMatcher.QUERY_KEY, "key=val"));

        assertThat(mockEnclosingRequest,
                new WhenMatcher(result));
    }

    @Test
    public void matchesIfRemoteAddrInXForwardedForHeader() throws IOException {
        when(mockEnclosingRequest.getHeaders("X-Forwarded-For")).thenReturn(
                new Header[]{new BasicHeader("X-Forwarded-For", "198.154.20.2, 127.0.0.1")});

        Map<String, String> result = new HashMap<>(WHEN_MAP);

        result.putAll(Map.of(WhenMatcher.REMOTE_ADDR_KEY, "127.0.0.1"));

        assertThat(mockEnclosingRequest,
                new WhenMatcher(result));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfScriptProvidedAndDoesNotMatch() {
        when(mockEnclosingRequest.getMethod()).thenReturn("GET");

        Map<String, String> result = new HashMap<>(WHEN_MAP);

        result.putAll(Map.of(WhenMatcher.SCRIPT_KEY, "(function(){return request.getMethod() == 'POST';})();"));

        assertThat(mockEnclosingRequest, new WhenMatcher(result));
    }

    @Test
    public void matchesIfScriptMatches() {
        when(mockEnclosingRequest.getURI()).thenReturn(URI.create("?key=val"));

        Map<String, String> result = new HashMap<>(WHEN_MAP);

        result.putAll(
                Map.of(WhenMatcher.SCRIPT_KEY, "(function(){return request.getURI().getQuery() == 'key=val';})();"));

        assertThat(mockEnclosingRequest, new WhenMatcher(result));
    }
}
