package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Matcher;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractStubRequestHandler implements StubRequestHandler {
    protected final Matcher<HttpServletRequest> matcher;

    public AbstractStubRequestHandler(Matcher<HttpServletRequest> matcher) {this.matcher = matcher;}

    public boolean matches(HttpServletRequest request) { return matcher.matches(request); }

    public abstract HttpResponse handle(HttpUriRequest request);
}
