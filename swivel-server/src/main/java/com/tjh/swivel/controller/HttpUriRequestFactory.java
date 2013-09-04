package com.tjh.swivel.controller;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;

public class HttpUriRequestFactory {
    public HttpRequestBase createGetRequest(URI uri, HttpServletRequest request) {
        HttpGet result = new HttpGet(createURI(uri, request));
        populateRequest(result, request);

        return result;
    }

    public HttpRequestBase createDeleteRequest(URI uri, HttpServletRequest request) {
        HttpDelete result = new HttpDelete(createURI(uri, request));
        populateRequest(result, request);

        return result;
    }

    public HttpRequestBase createPutRequest(URI uri, HttpServletRequest request) throws IOException {
        HttpPut result = new HttpPut(createURI(uri, request));
        populateRequest(result, request);
        setEntity(result, request);

        return result;
    }

    public HttpRequestBase createPostRequest(URI uri, HttpServletRequest request) throws IOException {
        HttpPost result = new HttpPost(createURI(uri, request));
        populateRequest(result, request);
        setEntity(result, request);

        return result;
    }

    protected URI createURI(URI uri, HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder(uri.getPath());
        String queryString = request.getQueryString();
        if (queryString != null) {
            stringBuilder.append("?")
                    .append(queryString);
        }
        return URI.create(stringBuilder.toString());
    }

    protected void populateRequest(HttpUriRequest uriRequest, HttpServletRequest servletRequest) {
        Enumeration headerNames = servletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            uriRequest.addHeader(headerName, servletRequest.getHeader(headerName));
        }
    }

    protected void setEntity(HttpEntityEnclosingRequest entityRequest, HttpServletRequest servletRequest)
            throws IOException {
        BufferedReader reader = servletRequest.getReader();
        int contentLength = servletRequest.getContentLength();
        StringBuilder stringBuilder = new StringBuilder(contentLength == -1 ? 5120 : contentLength);
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        entityRequest.setEntity(
                new StringEntity(stringBuilder.toString(), ContentType.parse(servletRequest.getContentType())));
    }
}
