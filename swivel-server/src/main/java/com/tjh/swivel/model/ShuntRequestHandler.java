package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;


public class ShuntRequestHandler {

    protected Logger logger = Logger.getLogger(ShuntRequestHandler.class);
    protected final URI baseUri;

    public ShuntRequestHandler(URI baseUri) {
        this.baseUri = baseUri;
    }

    public HttpResponse handle(HttpUriRequest request, HttpClient client) {
        try {
            URI localURI = request.getURI();
            HttpUriRequest shuntRequest = createShuntRequest(request);
            logger.debug(String.format("Shunting request %1$s to %2$s", localURI, shuntRequest.getURI()));
            return client.execute(shuntRequest);
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
