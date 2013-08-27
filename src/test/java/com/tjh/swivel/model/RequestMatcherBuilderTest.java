package com.tjh.swivel.model;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import vanderbilt.util.Maps;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RequestMatcherBuilderTest {

    public static final Map<String, Object> BODY = Maps.<String, Object>asMap(RequestMatcherBuilder.METHOD_KEY, "POST");

    private RequestMatcherBuilder requestMatcherBuilder;
    private HttpServletRequest expectationRequest;
    private HttpServletRequest actualRequest;

    @Before
    public void setUp() throws IOException {
        requestMatcherBuilder = new RequestMatcherBuilder();
        expectationRequest = mock(HttpServletRequest.class);
        actualRequest = mock(HttpServletRequest.class);

        requestMatcherBuilder.objectMapper = mock(ObjectMapper.class);

        when(expectationRequest.getPathInfo()).thenReturn("/extra/path");
        when(requestMatcherBuilder.objectMapper.readValue(any(InputStream.class), eq(Map.class)))
                .thenReturn(BODY);

        when(actualRequest.getRequestURI()).thenReturn("http://someServer/extra/path");
        when(actualRequest.getMethod()).thenReturn("POST");
    }

    @Test
    public void matchesPathAndMethod() throws IOException {
        //This is backwards from normal - expected is usually on the left.
        //but if I put the actual on the left, when the assertion fails, I get a nice
        //description of what's wrong
        assertThat(actualRequest, requestMatcherBuilder.buildMatcher(expectationRequest));
    }

    @Test
    public void buildMatcherDefersToBuildOptionalMatcher() throws IOException {
        RequestMatcherBuilder builderSpy = spy(requestMatcherBuilder);

        builderSpy.buildMatcher(expectationRequest);

        verify(builderSpy).buildOptionalMatcher(expectationRequest, BODY);
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfRemoteAddressProvidedAndDoesNotMatch() throws IOException {
        assertThat(actualRequest,
                requestMatcherBuilder
                        .buildOptionalMatcher(expectationRequest,
                                Maps.<String, Object>asMap(RequestMatcherBuilder.REMOTE_ADDR_KEY, "127.0.0.1")));
    }

    @Test
    public void matchesRemoteAddressIfProvidedMatches() throws IOException {
        when(actualRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        assertThat(actualRequest,
                requestMatcherBuilder
                        .buildOptionalMatcher(expectationRequest,
                                Maps.<String, Object>asMap(RequestMatcherBuilder.REMOTE_ADDR_KEY, "127.0.0.1")));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfContentTypeProvidedAndDoesNotMatch() throws IOException {
        assertThat(actualRequest,
                requestMatcherBuilder
                        .buildOptionalMatcher(expectationRequest,
                                Maps.<String, Object>asMap(RequestMatcherBuilder.CONTENT_TYPE_KEY,
                                        "application/json")));
    }

    @Test
    public void matchesIfContentTypeProvidedAndMatches() throws IOException {
        when(actualRequest.getContentType()).thenReturn("application/json");

        assertThat(actualRequest,
                requestMatcherBuilder
                        .buildOptionalMatcher(expectationRequest,
                                Maps.<String, Object>asMap(RequestMatcherBuilder.CONTENT_TYPE_KEY,
                                        "application/json")));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfContentProvidedAndDoesNotMatch() throws IOException {
        BufferedReader mockReader = mock(BufferedReader.class);
        when(actualRequest.getReader()).thenReturn(mockReader);

        assertThat(actualRequest,
                requestMatcherBuilder
                        .buildOptionalMatcher(expectationRequest,
                                Maps.<String, Object>asMap(RequestMatcherBuilder.CONTENT_KEY, "Yummy!")));
    }

    @Test
    public void matchesIfContentProvidedAndMatches() throws IOException {
        BufferedReader mockReader = mock(BufferedReader.class);
        when(actualRequest.getReader()).thenReturn(mockReader);
        when(mockReader.readLine()).thenReturn("Yummy!", null);

        assertThat(actualRequest,
                requestMatcherBuilder
                        .buildOptionalMatcher(expectationRequest,
                                Maps.<String, Object>asMap(RequestMatcherBuilder.CONTENT_KEY, "Yummy!")));
    }

    @Test(expected = AssertionError.class)
    public void matchFailsIfQueryProvidedAndDoesNotMatch() throws IOException {
        when(expectationRequest.getParameterMap()).thenReturn(Maps.asMap("key", new String[]{"val"}));

        assertThat(actualRequest, requestMatcherBuilder.buildOptionalMatcher(expectationRequest,
                Collections.<String, Object>emptyMap()));
    }

    @Test
    public void matchesIfQueryProvidedAndRequestContainsQuery() throws IOException {
        when(expectationRequest.getParameterMap()).thenReturn(Maps.asMap("key", new String[]{"val"}));
        when(actualRequest.getParameterMap()).thenReturn(Maps.asMap("key", new String[]{"val"}));


        assertThat(actualRequest, requestMatcherBuilder.buildOptionalMatcher(expectationRequest,
                Collections.<String, Object>emptyMap()));
    }

    @Test
    public void matchesIfRemoteAddrInXForwardedForHeader() throws IOException {
        when(actualRequest.getHeader("X-Forwarded-For")).thenReturn("198.154.20.2, 127.0.0.1");

        assertThat(actualRequest,
                requestMatcherBuilder
                        .buildOptionalMatcher(expectationRequest,
                                Maps.<String, Object>asMap(RequestMatcherBuilder.REMOTE_ADDR_KEY, "127.0.0.1")));
    }
}
