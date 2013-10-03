package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import java.net.URI;
import java.util.Map;

public interface RequestHandler<T extends HttpUriRequest> {
    HttpResponse handle(T request, URI matchedURI, HttpClient client);

    String description();

    Map<String, Object> toMap();
}
