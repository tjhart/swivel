package com.tjh.swivel.model;

import com.tjh.swivel.utils.ScriptWrapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import javax.script.Bindings;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.net.URI;
import java.util.Map;

public class DynamicStubRequestHandler extends AbstractStubRequestHandler {
    protected ScriptWrapper scriptWrapper;
    protected ResponseFactory responseFactory = new ResponseFactory();

    public DynamicStubRequestHandler(Map<String, Object> stubDescription) throws ScriptException {
        super(stubDescription);
        this.scriptWrapper = new ScriptWrapper((String)then.get(SCRIPT_KEY));
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
