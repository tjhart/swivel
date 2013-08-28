package com.tjh.swivel.controller;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.BasicHttpParams;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Enumeration;
import java.util.Map;

public class HttpUriRequestFactory {
    public HttpGet createGetRequest(URI uri, HttpServletRequest request) {
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

        //REDTAG:TJH - will params be in the URI?
        Map<String, String[]> parameterMap = servletRequest.getParameterMap();
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        for (String key : parameterMap.keySet()) {
            basicHttpParams.setParameter(key, parameterMap.get(key));
        }
        uriRequest.setParams(basicHttpParams);
    }
}
