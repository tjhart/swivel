package com.tjh.swivel.controller;

import org.apache.http.client.methods.HttpGet;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Enumeration;

public class HttpUriRequestFactory {
    public HttpGet createGetRequest(URI uri, HttpServletRequest request) {
        HttpGet result = new HttpGet(uri);
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            result.addHeader(headerName, request.getHeader(headerName));
        }
        return result;
    }

}
