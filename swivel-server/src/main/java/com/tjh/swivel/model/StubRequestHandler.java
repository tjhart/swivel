package com.tjh.swivel.model;

import org.apache.http.client.methods.HttpUriRequest;

public interface StubRequestHandler extends RequestHandler<HttpUriRequest> {

    boolean matches(HttpUriRequest request);

    int getId();

    void releaseResources();
}
