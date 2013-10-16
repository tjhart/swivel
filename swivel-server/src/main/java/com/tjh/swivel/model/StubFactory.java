package com.tjh.swivel.model;

import javax.script.ScriptException;
import java.util.Map;

public class StubFactory {
    public static final String THEN_KEY = "then";
    public static final String WHEN_KEY = "when";
    public static final String SCRIPT_KEY = "script";
    public static final String DESCRIPTION_KEY = "description";
    private ResponseFactory responseFactory;

    @SuppressWarnings("unchecked")
    public StubRequestHandler createStubFor(Map<String, Object> stubMap) throws ScriptException {
        //TODO:TJH - add a description entry in the top level map - use that
        //description when displaying on the GUI
        StubRequestHandler result;
        String description = (String) stubMap.get(DESCRIPTION_KEY);
        WhenMatcher matcher = new WhenMatcher((Map<String, String>) stubMap.get(WHEN_KEY));
        Map<String, Object> then = (Map<String, Object>) stubMap.get(THEN_KEY);
        if (then.containsKey(SCRIPT_KEY)) {
            result = new DynamicStubRequestHandler(description, matcher, (String) then.get(SCRIPT_KEY));
        } else {
            result = new StaticStubRequestHandler(description, matcher, responseFactory.createResponse(then), then);
        }
        return result;
    }

    public void setResponseFactory(ResponseFactory responseFactory) { this.responseFactory = responseFactory; }
}
