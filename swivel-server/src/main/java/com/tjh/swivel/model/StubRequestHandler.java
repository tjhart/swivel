package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

public interface StubRequestHandler {
    HttpResponse handle(HttpUriRequest request);

    boolean matches(HttpUriRequest request);

    int getId();
}
