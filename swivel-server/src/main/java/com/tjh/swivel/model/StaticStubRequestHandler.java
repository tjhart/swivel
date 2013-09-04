package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Matcher;

public class StaticStubRequestHandler extends AbstractStubRequestHandler {
    protected final HttpResponse httpResponse;

    public StaticStubRequestHandler(Matcher<HttpUriRequest> matcher, HttpResponse httpResponse) {
        super(matcher);
        this.httpResponse = httpResponse;
    }

    @Override
    public HttpResponse handle(HttpUriRequest request) { return httpResponse; }
}
