package com.tjh.swivel.model;

import com.tjh.swivel.utils.ScriptWrapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Matcher;

import javax.script.Bindings;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.servlet.http.HttpServletRequest;

public class DynamicStubRequestHandler extends AbstractStubRequestHandler {
    protected ScriptWrapper scriptWrapper;
    protected ResponseFactory responseFactory = new ResponseFactory();

    public DynamicStubRequestHandler(Matcher<HttpServletRequest> matcher, String sourceScript,
            String engineName) throws ScriptException {
        super(matcher);
        this.scriptWrapper = new ScriptWrapper(engineName, sourceScript);
    }

    public DynamicStubRequestHandler(Matcher<HttpServletRequest> matcher, String sourceScript) throws ScriptException {
        this(matcher, sourceScript, "javascript");
    }

    @Override
    public HttpResponse handle(HttpUriRequest request) {
        try {
            Bindings bindings = new SimpleBindings();
            bindings.put("request", request);
            bindings.put("responseFactory", responseFactory);

            return (HttpResponse) scriptWrapper.evalWith(bindings);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }
}
