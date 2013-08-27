package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

public class StaticResponseHandler implements ResponseHandler {
    protected final HttpResponse httpResponse;

    public
    StaticResponseHandler(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public HttpResponse handle(HttpUriRequest request) {
        return httpResponse;
    }
}
