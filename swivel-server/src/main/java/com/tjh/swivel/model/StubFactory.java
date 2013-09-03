package com.tjh.swivel.model;

import org.hamcrest.Matcher;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Map;

public class StubFactory {
    public static final String THEN_KEY = "then";
    public static final String WHEN_KEY = "when";
    public static final String SCRIPT_KEY = "script";
    private MatcherFactory matcherFactory;
    private ResponseFactory responseFactory;

    @SuppressWarnings("unchecked")
    public StubRequestHandler createStubFor(URI localURI, Map<String, Object> stubDescription) throws ScriptException {
        StubRequestHandler result;
        Matcher<HttpServletRequest> matcher =
                matcherFactory.buildMatcher(localURI, (Map<String, String>) stubDescription.get(WHEN_KEY));
        Map<String, Object> thenMap = (Map<String, Object>) stubDescription.get(THEN_KEY);
        if (thenMap.containsKey(SCRIPT_KEY)) {
            result = new DynamicStubRequestHandler(matcher, (String) thenMap.get(SCRIPT_KEY));
        } else {
            result = new StaticStubRequestHandler(matcher, responseFactory.createResponse(thenMap));
        }
        return result;
    }

    public void setResponseFactory(ResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    public void setMatcherFactory(MatcherFactory matcherFactory) { this.matcherFactory = matcherFactory; }
}
