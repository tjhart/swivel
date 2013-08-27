package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

public interface ResponseHandler {
    HttpResponse handle(HttpUriRequest request);
}
