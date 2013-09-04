package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import javax.servlet.http.HttpServletRequest;

public interface StubRequestHandler {
    //YELLOWTAG:TJH - should this be a raw HttpServletRequest instead?
    HttpResponse handle(HttpUriRequest request);

    boolean matches(HttpServletRequest request);

    int getId();
}
