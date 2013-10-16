package com.tjh.swivel.model;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MatcherFactoryTest {

    public static final Map<String, String> STUB_DESCRIPTION = Maps.asMap(MatcherFactory.METHOD_KEY, "POST");
    public static final String APPLICATION_JSON = ContentType.APPLICATION_JSON.toString();
    public static final String CONTENT = "fred";
    public static final StringEntity STRING_ENTITY = new StringEntity(CONTENT, ContentType.APPLICATION_JSON);

    private MatcherFactory matcherFactory;
    private HttpUriRequest actualRequest;
    private HttpEntityEnclosingRequestBase mockEnclosingRequest;

    @Before
    public void setUp() throws IOException {
        matcherFactory = new MatcherFactory();
        actualRequest = mock(HttpUriRequest.class);
        mockEnclosingRequest = mock(HttpEntityEnclosingRequestBase.class);

        when(actualRequest.getURI()).thenReturn(URI.create("some/path"));
        when(actualRequest.getMethod()).thenReturn("POST");
        when(mockEnclosingRequest.getEntity()).thenReturn(STRING_ENTITY);

    }

    @Test
    public void matchesMethod() throws IOException {
        //This is backwards from normal - expected is usually on the left.
        //but if I put the actual on the left, when the assertion fails, I get a nice
        //description of what's wrong
        assertThat(actualRequest, matcherFactory.buildMatcher(STUB_DESCRIPTION));
    }

    @Test
    public void buildMatcherDefersToBuildOptionalMatcher() throws IOException {
        MatcherFactory builderSpy = spy(matcherFactory);

        builderSpy.buildMatcher(STUB_DESCRIPTION);

        verify(builderSpy).buildOptionalMatcher(STUB_DESCRIPTION);
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfContentTypeProvidedAndDoesNotMatch() throws IOException {
        assertThat(actualRequest,
                matcherFactory
                        .buildOptionalMatcher(
                                Maps.asMap(MatcherFactory.CONTENT_TYPE_KEY, APPLICATION_JSON)));
    }

    @Test
    public void matchesIfContentTypeProvidedAndMatches() throws IOException {
        assertThat(mockEnclosingRequest,
                matcherFactory
                        .buildOptionalMatcher(
                                Maps.asMap(MatcherFactory.CONTENT_TYPE_KEY, APPLICATION_JSON)));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfContentProvidedAndDoesNotMatch() throws IOException {
        assertThat(actualRequest,
                matcherFactory.buildOptionalMatcher(Maps.asMap(MatcherFactory.CONTENT_KEY, "Yummy!")));
    }

    @Test
    public void matchesIfContentProvidedAndMatches() throws IOException {
        assertThat(mockEnclosingRequest,
                matcherFactory.buildOptionalMatcher(
                        Maps.asMap(MatcherFactory.CONTENT_KEY, CONTENT)));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfQueryProvidedAndDoesNotMatch() throws IOException {
        assertThat(actualRequest,
                matcherFactory
                        .buildOptionalMatcher(
                                Maps.asMap(MatcherFactory.QUERY_KEY, "key=val")));
    }

    @Test
    public void matchesIfQueryProvidedAndRequestContainsQuery() throws IOException {
        when(actualRequest.getURI()).thenReturn(URI.create("some/path?key=val"));

        assertThat(actualRequest,
                matcherFactory
                        .buildOptionalMatcher(
                                Maps.asMap(MatcherFactory.QUERY_KEY, "key=val")));
    }

    @Test
    public void matchesIfRemoteAddrInXForwardedForHeader() throws IOException {
        when(actualRequest.getHeaders("X-Forwarded-For")).thenReturn(
                new Header[]{new BasicHeader("X-Forwarded-For", "198.154.20.2, 127.0.0.1")});

        assertThat(actualRequest,
                matcherFactory
                        .buildOptionalMatcher(Maps.asMap(MatcherFactory.REMOTE_ADDR_KEY, "127.0.0.1")));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfScriptProvidedAndDoesNotMatch() {
        when(actualRequest.getMethod()).thenReturn("GET");

        assertThat(actualRequest,
                matcherFactory
                        .buildOptionalMatcher(Maps.asMap(MatcherFactory.SCRIPT_KEY,
                                "(function(){return request.getMethod() == 'POST';})();")));
    }

    @Test
    public void matchesIfScriptMatches() {
        when(actualRequest.getMethod()).thenReturn("GET");

        assertThat(actualRequest,
                matcherFactory
                        .buildOptionalMatcher(Maps.asMap(MatcherFactory.SCRIPT_KEY,
                                "(function(){return request.getMethod() == 'GET';})();")));
    }
}
