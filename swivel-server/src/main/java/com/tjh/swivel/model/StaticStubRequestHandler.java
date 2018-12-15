package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URI;
import java.util.Map;

public class StaticStubRequestHandler extends AbstractStubRequestHandler {

    public static final Logger LOGGER = Logger.getLogger(StaticStubRequestHandler.class);

    protected final HttpResponse httpResponse;
    protected final File responseFile;

    public StaticStubRequestHandler(Map<String, Object> stubDescription) {
        super(stubDescription);
        if (then.containsKey(STORAGE_PATH_KEY)) {
            responseFile = new File((String) then.get(STORAGE_PATH_KEY));
            if (!responseFile.exists() && !responseFile.isFile()) {
                throw new RuntimeException("Stub Storage file does not exist:" + responseFile.getPath());
            }
            this.httpResponse = responseFactory.createResponse(then, responseFile);
        } else {
            responseFile = null;
            this.httpResponse = responseFactory.createResponse(then);
        }
    }

    @Override
    public HttpResponse handle(HttpUriRequest request, URI matchedURI, HttpClient client) { return httpResponse; }

    @Override
    public void releaseResources() {
        if (responseFile != null) {
            LOGGER.debug("removing storage file for " + toString());
            if (!responseFile.delete()) {
                LOGGER.warn("Could not delete storage for responseFile: " + responseFile.getPath());
            }
        }
    }

    @Override
    public String getResourcePath() { return responseFile == null ? null : responseFile.getPath(); }

    @Override
    public String toString() {
        return "StaticStubRequestHandler{" +
                "super=" + super.toString() +
                ", httpResponse=" + httpResponse +
                '}';
    }
}
