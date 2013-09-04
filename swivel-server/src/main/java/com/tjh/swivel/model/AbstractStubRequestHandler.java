package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Matcher;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractStubRequestHandler implements StubRequestHandler {
    protected final Matcher<HttpUriRequest> matcher;

    public AbstractStubRequestHandler(Matcher<HttpUriRequest> matcher) {this.matcher = matcher;}

    @Override
    public boolean matches(HttpServletRequest request) { return matcher.matches(request); }

    @Override
    public int getId() { return System.identityHashCode(this); }

    public abstract HttpResponse handle(HttpUriRequest request);
}
