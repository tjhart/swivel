package com.tjh.swivel.model;

import com.tjh.swivel.utils.URIUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;


public class ShuntRequestHandler implements RequestHandler<HttpRequestBase> {

    public static final String CONTENT_BASE_HEADER = "Content-base";
    protected Logger logger = Logger.getLogger(ShuntRequestHandler.class);
    protected final URI baseUri;

    public ShuntRequestHandler(URI baseUri) { this.baseUri = baseUri; }

    public HttpResponse handle(HttpRequestBase request, URI matchedURI, HttpClient client) {
        try {
            URI localURI = request.getURI();
            HttpUriRequest shuntRequest = createShuntRequest(request, matchedURI);
            logger.debug(String.format("Shunting request %1$s to %2$s", localURI, shuntRequest.getURI()));
            HttpResponse response = client.execute(shuntRequest);
            response.addHeader(CONTENT_BASE_HEADER, this.baseUri.toString());
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected HttpUriRequest createShuntRequest(HttpRequestBase request, URI matchedURI) {
        request.setURI(URIUtils.resolveUri(baseUri, request.getURI(), matchedURI));

        return request;
    }

    @Override
    public String description() { return "Shunting to" + baseUri; }

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
