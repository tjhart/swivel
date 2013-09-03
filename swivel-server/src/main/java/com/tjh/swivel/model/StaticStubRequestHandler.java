package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

public class StaticStubRequestHandler implements StubRequestHandler {
    protected final HttpResponse httpResponse;

    public StaticStubRequestHandler(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public HttpResponse handle(HttpUriRequest request) {
        return httpResponse;
    }
}
