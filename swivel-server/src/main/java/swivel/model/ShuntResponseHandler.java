package swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.net.URI;

public class ShuntResponseHandler implements ResponseHandler {
    protected final URI baseUri;
    protected final HttpClient client;

    public ShuntResponseHandler(URI baseUri, HttpClient client) {
        this.baseUri = baseUri;
        this.client = client;
    }

    @Override
    public HttpResponse handle(HttpUriRequest request) {
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
}
