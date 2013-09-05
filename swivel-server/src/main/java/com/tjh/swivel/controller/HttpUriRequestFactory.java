package com.tjh.swivel.controller;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import vanderbilt.util.Sets;
import vanderbilt.util.Strings;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class HttpUriRequestFactory {

    public static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
    public static final Set<String> IGNORED_HEADERS = Sets.asConstantSet("Content-Length");

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

    public HttpRequestBase createPutRequest(URI uri, HttpServletRequest request, byte[] body) throws IOException {
        HttpPut result = new HttpPut(createURI(uri, request));
        populateRequest(result, request);
        result.setEntity(new ByteArrayEntity(body));

        return result;
    }

    public HttpRequestBase createPostRequest(URI uri, HttpServletRequest request, byte[] body) throws IOException {
        HttpPost result = new HttpPost(createURI(uri, request));
        populateRequest(result, request);
        result.setEntity(new ByteArrayEntity(body));

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
            //some headers will be populated in the library
            if (!IGNORED_HEADERS.contains(headerName)) {
                uriRequest.addHeader(headerName, servletRequest.getHeader(headerName));
            }
        }

        //TODO:TJH - verify Swivel is populating all header fields as a good proxy should
        Header header = uriRequest.getFirstHeader(X_FORWARDED_FOR_HEADER);
        List<String> origValue = new ArrayList<String>(2);
        if (header != null) {
            origValue.add(header.getValue());
            uriRequest.removeHeader(header);
        }
        origValue.add(servletRequest.getRemoteAddr());
        uriRequest.addHeader(X_FORWARDED_FOR_HEADER,
                Strings.join(origValue.toArray(new String[origValue.size()]), ", "));
    }
}
