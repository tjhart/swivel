package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.net.URI;


public class ShuntRequestHandler {
    protected final URI baseUri;

    public ShuntRequestHandler(URI baseUri) {
        this.baseUri = baseUri;
    }

    public HttpResponse handle(HttpUriRequest request, HttpClient client) {
        try {
            return client.execute(createShuntRequest(request));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected HttpUriRequest createShuntRequest(HttpUriRequest request) {
        HttpRequestBase result = (HttpRequestBase) request;

        result.setURI(baseUri.resolve(result.getURI()));

        return result;
    }

    //<editor-fold desc="Object">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShuntRequestHandler)) return false;

        ShuntRequestHandler that = (ShuntRequestHandler) o;

        return baseUri.equals(that.baseUri);
    }

    @Override
    public int hashCode() { return baseUri.hashCode(); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ShuntRequestHandler{");
        sb.append("baseUri=").append(baseUri);
        sb.append('}');
        return sb.toString();
    }
    //</editor-fold>
}