package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Matcher;

import java.net.URI;

public class StaticStubRequestHandler extends AbstractStubRequestHandler {
    protected final HttpResponse httpResponse;

    public StaticStubRequestHandler(Matcher<HttpUriRequest> matcher, HttpResponse httpResponse) {
        super(matcher);
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
