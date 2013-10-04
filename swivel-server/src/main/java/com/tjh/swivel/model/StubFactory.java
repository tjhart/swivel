package com.tjh.swivel.model;

import javax.script.ScriptException;
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
        //TODO:TJH - add a description entry in the top level map - use that
        //description when displaying on the GUI
        StubRequestHandler result;
        WhenMatcher matcher =
                matcherFactory.buildMatcher(localURI, (Map<String, String>) stubDescription.get(WHEN_KEY));
        Map<String, Object> then = (Map<String, Object>) stubDescription.get(THEN_KEY);
        if (then.containsKey(SCRIPT_KEY)) {
            result = new DynamicStubRequestHandler(matcher, (String) then.get(SCRIPT_KEY));
        } else {
            result = new StaticStubRequestHandler(matcher, responseFactory.createResponse(then), then);
        }
        return result;
    }

    public void setResponseFactory(ResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    public void setMatcherFactory(MatcherFactory matcherFactory) { this.matcherFactory = matcherFactory; }
}
