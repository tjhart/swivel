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
    protected ResponseFactory responseFactory;

    public DynamicStubRequestHandler(Map<String, Object> stubDescription,
            ResponseFactory responseFactory) throws ScriptException {
        super(stubDescription, responseFactory);
        this.scriptWrapper = new ScriptWrapper((String)then.get(SCRIPT_KEY));
        this.responseFactory = responseFactory;
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
        return "DynamicStubRequestHandler{" +
                "super=" + super.toString() +
                ", scriptWrapper=" + scriptWrapper +
                '}';
    }
}
