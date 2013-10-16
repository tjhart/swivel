package com.tjh.swivel.model;

import com.tjh.swivel.utils.URIUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;
import vanderbilt.util.Maps;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;


public class ShuntRequestHandler implements RequestHandler<HttpRequestBase> {

    public static final String CONTENT_BASE_HEADER = "Content-Base";
    public static final String REMOTE_URL_KEY = "remoteURL";
    protected Logger logger = Logger.getLogger(ShuntRequestHandler.class);
    protected final URL remoteURL;

    public ShuntRequestHandler(URL remoteURL) { this.remoteURL = remoteURL; }

    public HttpResponse handle(HttpRequestBase request, URI matchedURI, HttpClient client) {
        try {
            URI localURI = request.getURI();
            HttpUriRequest shuntRequest = createShuntRequest(request, matchedURI);
            logger.debug(String.format("Shunting request %1$s to %2$s", localURI, shuntRequest.getURI()));
            HttpResponse response = client.execute(shuntRequest);
            response.addHeader(CONTENT_BASE_HEADER, this.remoteURL.toString());
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected HttpUriRequest createShuntRequest(HttpRequestBase request, URI matchedURI) {
        try {
            request.setURI(URIUtils.resolveUri(remoteURL.toURI(), request.getURI(), matchedURI));
            return request;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String description() { return "Shunting to" + remoteURL; }

    @Override
    public Map<String, Object> toMap() {
        return Maps.<String, Object>asMap(REMOTE_URL_KEY, remoteURL.toString());
    }

    //<editor-fold desc="Object">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShuntRequestHandler)) return false;

        ShuntRequestHandler that = (ShuntRequestHandler) o;

        return remoteURL.equals(that.remoteURL);
    }

    @Override
    public int hashCode() { return remoteURL.hashCode(); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ShuntRequestHandler{");
        sb.append("remoteURL=").append(remoteURL);
        sb.append('}');
        return sb.toString();
    }
    //</editor-fold>
}
