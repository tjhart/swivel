package com.tjh.swivel.controller;

import com.tjh.swivel.model.Configuration;
import com.tjh.swivel.model.RequestHandler;
import com.tjh.swivel.model.ResponseFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import vanderbilt.util.Strings;

import java.net.URI;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

public class RequestRouter {

    private static Logger LOGGER = Logger.getLogger(RequestRouter.class);

    private Configuration configuration;
    private ClientConnectionManager clientConnectionManager;
    private HttpParams httpParams = new BasicHttpParams();
    private ResponseFactory responseFactory;

    public HttpResponse route(HttpRequestBase request) {
        LOGGER.debug("Routing " + request);
        RequestHandler requestHandler;
        Deque<String> pathElements = new LinkedList<String>(Arrays.asList(toKeys(request.getURI())));
        String matchedPath;
        do {
            matchedPath = Strings.join(pathElements.toArray(new String[pathElements.size()]), "/");
            requestHandler = configuration.findRequestHandler(request, matchedPath);
            pathElements.removeLast();
        }
        while (requestHandler == null && !pathElements.isEmpty());

        return createResponse(request, requestHandler, matchedPath);
    }

    @SuppressWarnings("unchecked")
    private HttpResponse createResponse(HttpRequestBase request, RequestHandler requestHandler, String matchedPath) {
        HttpResponse response;
        if (requestHandler == null) {
            LOGGER.debug("No handler found");
            response = responseFactory.createResponse(404
            );
        } else {

            LOGGER.debug(String.format("Routing <%1$s> to <%2$s>. Matched path is: <%3$s>", request, requestHandler,
                    matchedPath));
            response = requestHandler.handle(request, URI.create(matchedPath), createClient());
        }
        return response;
    }

    protected HttpClient createClient() { return new DefaultHttpClient(clientConnectionManager, httpParams); }

    private String[] toKeys(URI localURI) {return localURI.getPath().split("/");}

    //<editor-fold desc="bean">
    public void setClientConnectionManager(ClientConnectionManager clientConnectionManager) {
        this.clientConnectionManager = clientConnectionManager;
    }

    public void setResponseFactory(ResponseFactory responseFactory) { this.responseFactory = responseFactory; }

    public void setConfiguration(Configuration configuration) { this.configuration = configuration; }

    public void setHttpParams(HttpParams httpParams) { this.httpParams = httpParams; }
    //</editor-fold>
}
