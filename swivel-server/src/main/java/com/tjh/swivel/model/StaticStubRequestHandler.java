package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import java.net.URI;
import java.util.Map;

public class StaticStubRequestHandler extends AbstractStubRequestHandler {
    protected final HttpResponse httpResponse;

    public StaticStubRequestHandler(WhenMatcher matcher, HttpResponse httpResponse,
            Map<String, Object> then) {
        super(matcher, then);
        this.httpResponse = httpResponse;
    }

    @Override
    public HttpResponse handle(HttpUriRequest request, URI matchedURI, HttpClient client) { return httpResponse; }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StaticStubRequestHandler{");
        sb.append("super=").append(super.toString());
        sb.append(", httpResponse=").append(httpResponse);
        sb.append('}');
        return sb.toString();
    }
}
