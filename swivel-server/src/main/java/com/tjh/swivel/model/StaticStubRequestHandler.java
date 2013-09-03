package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Matcher;

import javax.servlet.http.HttpServletRequest;

public class StaticStubRequestHandler extends AbstractStubRequestHandler {
    protected final HttpResponse httpResponse;

    public StaticStubRequestHandler(Matcher<HttpServletRequest> matcher, HttpResponse httpResponse) {
        super(matcher);
        this.httpResponse = httpResponse;
    }

    @Override
    public HttpResponse handle(HttpUriRequest request) { return httpResponse; }
}
