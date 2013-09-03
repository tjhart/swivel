package com.tjh.swivel.model;

import org.hamcrest.Matcher;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Map;

public class StubFactory {
    public static final String THEN_RETURN_KEY = "thenReturn";
    public static final String WHEN_KEY = "when";
    private MatcherFactory matcherFactory;
    private ResponseFactory responseFactory;

    @SuppressWarnings("unchecked")
    public StubRequestHandler createStubFor(URI localURI, Map<String, Object> stubDescription) {
        StubRequestHandler result = null;
        Matcher<HttpServletRequest> matcher = matcherFactory.buildMatcher(localURI,
                (Map<String, String>) stubDescription.get(WHEN_KEY));
        if (stubDescription.containsKey(THEN_RETURN_KEY)) {
            result = new StaticStubRequestHandler(matcher,
                    responseFactory.createResponse((Map<String, Object>) stubDescription.get(THEN_RETURN_KEY)));
        }
        return result;
    }

    public void setResponseFactory(ResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    public void setMatcherFactory(MatcherFactory matcherFactory) { this.matcherFactory = matcherFactory; }
}
