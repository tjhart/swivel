package com.tjh.swivel.model;

import com.tjh.swivel.utils.ScriptWrapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import vanderbilt.util.Maps;

import javax.script.Bindings;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.net.URI;

public class DynamicStubRequestHandler extends AbstractStubRequestHandler {
    protected ScriptWrapper scriptWrapper;
    protected ResponseFactory responseFactory = new ResponseFactory();

    public DynamicStubRequestHandler(WhenMatcher matcher, String sourceScript,
            String engineName) throws ScriptException {
        super(matcher, Maps.<String, Object>asMap("script", sourceScript));
        this.scriptWrapper = new ScriptWrapper(engineName, sourceScript);
    }

    public DynamicStubRequestHandler(WhenMatcher matcher, String sourceScript) throws ScriptException {
        this(matcher, sourceScript, "javascript");
    }

    @Override
    public HttpResponse handle(HttpUriRequest request, URI matchedURI, HttpClient client) {
        try {
            Bindings bindings = new SimpleBindings();
            bindings.put("request", request);
            bindings.put("matchedURI", matchedURI.toString());
            bindings.put("client", client);
            bindings.put("responseFactory", responseFactory);

            return (HttpResponse) scriptWrapper.evalWith(bindings);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DynamicStubRequestHandler{");
        sb.append("super=").append(super.toString());
        sb.append(", scriptWrapper=").append(scriptWrapper);
        sb.append('}');
        return sb.toString();
    }
}
