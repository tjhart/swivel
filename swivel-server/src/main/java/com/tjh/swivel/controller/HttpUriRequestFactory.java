package com.tjh.swivel.controller;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Enumeration;

public class HttpUriRequestFactory {
    public HttpUriRequest createGetRequest(URI uri, HttpServletRequest request) {
        HttpGet result = new HttpGet(uri);
        populateRequest(result, request);

        return result;
    }

    protected void populateRequest(HttpUriRequest uriRequest, HttpServletRequest servletRequest) {
        Enumeration headerNames = servletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            uriRequest.addHeader(headerName, servletRequest.getHeader(headerName));
        }
    }
}
