package com.tjh.swivel.model;

import javax.script.ScriptException;
import java.util.Map;

public class StubRequestHandlerFactory {
    public static final String THEN_KEY = "then";
    public static final String SCRIPT_KEY = "script";

    private final ResponseFactory responseFactory;

    public StubRequestHandlerFactory(ResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    public StubRequestHandler createStubFor(Map<String, Object> stubDescription) throws ScriptException {
        StubRequestHandler result;
        //noinspection unchecked
        if (((Map<String, Object>) stubDescription.get(THEN_KEY)).containsKey(SCRIPT_KEY)) {
            result = new DynamicStubRequestHandler(stubDescription, responseFactory);
        } else {
            result = new StaticStubRequestHandler(stubDescription, responseFactory);
        }
        return result;
    }
}
