package com.tjh.swivel.controller;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class HttpUriRequestFactory {

    public static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
    public static final Set<String> IGNORED_HEADERS = Set.of(HttpHeaders.HOST, HttpHeaders.CONTENT_LENGTH);
    private static Logger LOGGER = Logger.getLogger(HttpUriRequestFactory.class);

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

    public HttpRequestBase createPutRequest(URI uri, HttpServletRequest request, String body)  {
        HttpPut result = new HttpPut(createURI(uri, request));
        populateRequest(result, request);
        result.setEntity(new StringEntity(body, ContentType.create(request.getContentType())));

        return result;
    }

    public HttpRequestBase createPostRequest(URI uri, HttpServletRequest request, String body) {
        HttpPost result = new HttpPost(createURI(uri, request));
        populateRequest(result, request);
        result.setEntity(new StringEntity(body, ContentType.create(request.getContentType())));

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
            //some headers will be populated during execution of the request
            if (!IGNORED_HEADERS.contains(headerName)) {
                uriRequest.addHeader(headerName, servletRequest.getHeader(headerName));
            }
        }

        //TODO:TJH - verify Swivel is managing header fields as a good proxy should
        Header header = uriRequest.getFirstHeader(X_FORWARDED_FOR_HEADER);
        List<String> origValue = new ArrayList<>(2);
        if (header != null) {
            origValue.add(header.getValue());
            uriRequest.removeHeader(header);
        }
        origValue.add(servletRequest.getRemoteAddr());
        uriRequest.addHeader(X_FORWARDED_FOR_HEADER, String.join(", ",origValue));
    }
}
