package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Matcher;

import java.net.URI;

public abstract class AbstractStubRequestHandler implements StubRequestHandler {
    protected final Matcher<HttpUriRequest> matcher;

    public AbstractStubRequestHandler(Matcher<HttpUriRequest> matcher) {this.matcher = matcher;}

    @Override
    public boolean matches(HttpUriRequest request) { return matcher.matches(request); }

    @Override
    public int getId() { return System.identityHashCode(this); }

    public abstract HttpResponse handle(HttpUriRequest request, URI matchedURI, HttpClient client);
}
