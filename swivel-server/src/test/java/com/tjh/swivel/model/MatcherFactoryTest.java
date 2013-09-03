package com.tjh.swivel.model;

import org.junit.Before;
import org.junit.Test;
import vanderbilt.util.Maps;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MatcherFactoryTest {

    public static final Map<String, String> STUB_DESCRIPTION = Maps.asMap(MatcherFactory.METHOD_KEY, "POST");
    public static final URI LOCAL_URI = URI.create("some/path");
    public static final String APPLICATION_JSON = "application/json";

    private MatcherFactory matcherFactory;
    private HttpServletRequest actualRequest;

    @Before
    public void setUp() throws IOException {
        matcherFactory = new MatcherFactory();
        actualRequest = mock(HttpServletRequest.class);

        when(actualRequest.getRequestURI()).thenReturn("some/path");
        when(actualRequest.getMethod()).thenReturn("POST");
    }

    @Test
    public void matchesPathAndMethod() throws IOException {
        //This is backwards from normal - expected is usually on the left.
        //but if I put the actual on the left, when the assertion fails, I get a nice
        //description of what's wrong
        assertThat(actualRequest, matcherFactory.buildMatcher(LOCAL_URI, STUB_DESCRIPTION));
    }

    @Test
    public void buildMatcherDefersToBuildOptionalMatcher() throws IOException {
        System.out.println("LOCAL_URI.getQuery() = " + LOCAL_URI.getQuery());
        MatcherFactory builderSpy = spy(matcherFactory);

        builderSpy.buildMatcher(LOCAL_URI, STUB_DESCRIPTION);

        verify(builderSpy).buildOptionalMatcher(LOCAL_URI, STUB_DESCRIPTION);
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfRemoteAddressProvidedAndDoesNotMatch() throws IOException {
        assertThat(actualRequest,
                matcherFactory
                        .buildOptionalMatcher(LOCAL_URI, Maps.asMap(MatcherFactory.REMOTE_ADDR_KEY, "127.0.0.1")));
    }

    @Test
    public void matchesRemoteAddressIfProvidedAndMatches() throws IOException {
        when(actualRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        assertThat(actualRequest, matcherFactory.buildOptionalMatcher(LOCAL_URI,
                Maps.asMap(MatcherFactory.REMOTE_ADDR_KEY, "127.0.0.1")));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfContentTypeProvidedAndDoesNotMatch() throws IOException {
        assertThat(actualRequest,
                matcherFactory
                        .buildOptionalMatcher(LOCAL_URI,
                                Maps.asMap(MatcherFactory.CONTENT_TYPE_KEY, APPLICATION_JSON)));
    }

    @Test
    public void matchesIfContentTypeProvidedAndMatches() throws IOException {
        when(actualRequest.getContentType()).thenReturn(APPLICATION_JSON);

        assertThat(actualRequest,
                matcherFactory
                        .buildOptionalMatcher(LOCAL_URI,
                                Maps.asMap(MatcherFactory.CONTENT_TYPE_KEY, APPLICATION_JSON)));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfContentProvidedAndDoesNotMatch() throws IOException {
        BufferedReader mockReader = mock(BufferedReader.class);
        when(actualRequest.getReader()).thenReturn(mockReader);

        assertThat(actualRequest,
                matcherFactory.buildOptionalMatcher(LOCAL_URI, Maps.asMap(MatcherFactory.CONTENT_KEY, "Yummy!")));
    }

    @Test
    public void matchesIfContentProvidedAndMatches() throws IOException {
        BufferedReader mockReader = mock(BufferedReader.class);
        when(actualRequest.getReader()).thenReturn(mockReader);
        when(mockReader.readLine()).thenReturn("Yummy!", null);

        assertThat(actualRequest,
                matcherFactory.buildOptionalMatcher(LOCAL_URI, Maps.asMap(MatcherFactory.CONTENT_KEY, "Yummy!")));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfQueryProvidedAndDoesNotMatch() throws IOException {
        assertThat(actualRequest,
                matcherFactory
                        .buildOptionalMatcher(URI.create(LOCAL_URI + "?" + "key=val"),
                                Collections.<String, String>emptyMap()));
    }

    @Test
    public void matchesIfQueryProvidedAndRequestContainsQuery() throws IOException {
        when(actualRequest.getParameterMap()).thenReturn(Maps.asMap("key", new String[]{"val"}));


        assertThat(actualRequest,
                matcherFactory
                        .buildOptionalMatcher(URI.create(LOCAL_URI + "?" + "key=val"),
                                Collections.<String, String>emptyMap()));
    }

    @Test
    public void matchesIfRemoteAddrInXForwardedForHeader() throws IOException {
        when(actualRequest.getHeader("X-Forwarded-For")).thenReturn("198.154.20.2, 127.0.0.1");

        assertThat(actualRequest,
                matcherFactory
                        .buildOptionalMatcher(LOCAL_URI, Maps.asMap(MatcherFactory.REMOTE_ADDR_KEY, "127.0.0.1")));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfScriptProvidedAndDoesNotMatch() {
        when(actualRequest.getLocalPort()).thenReturn(24);

        assertThat(actualRequest,
                matcherFactory
                        .buildOptionalMatcher(LOCAL_URI, Maps.asMap(MatcherFactory.SCRIPT_KEY,
                                "(function(){return request.getLocalPort() === 23;})();")));
    }

    @Test
    public void matchesIfScriptMatches() {
        when(actualRequest.getLocalPort()).thenReturn(23);

        assertThat(actualRequest,
                matcherFactory
                        .buildOptionalMatcher(LOCAL_URI, Maps.asMap(MatcherFactory.SCRIPT_KEY,
                                "(function(){return request.getLocalPort() === 23;})();")));
    }
}
