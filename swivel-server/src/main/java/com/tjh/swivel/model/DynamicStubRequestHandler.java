package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import com.tjh.swivel.utils.ScriptWrapper;

import javax.script.Bindings;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

public class DynamicStubRequestHandler implements StubRequestHandler {
    protected ScriptWrapper scriptWrapper;
    protected ResponseFactory responseFactory = new ResponseFactory();

    public DynamicStubRequestHandler(String sourceScript, String engineName) throws ScriptException {
        this.scriptWrapper = new ScriptWrapper(engineName, sourceScript);
    }

    public DynamicStubRequestHandler(String sourceScript) throws ScriptException {
        this(sourceScript, "javascript");
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
