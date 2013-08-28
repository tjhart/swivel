package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

public class StaticRequestHandler implements RequestHandler {
    protected final HttpResponse httpResponse;

    public StaticRequestHandler(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public HttpResponse handle(HttpUriRequest request) {
        return httpResponse;
    }
}
