package com.tjh.swivel.controller;

import org.apache.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Path("proxy")
public class ProxyResource {
    private RequestRouter router;
    private HttpUriRequestFactory requestFactory;
    private JerseyResponseFactory responseFactory;

    @GET
    @Path("{localPath: .*}")
    public Response get(@PathParam("localPath") String localPath, @Context HttpServletRequest request)
            throws URISyntaxException, IOException {
        //REDTAG:TJH - will query parameters be in 'localPath'?
        HttpResponse response = router.work(requestFactory.createGetRequest(new URI(localPath), request));
        return responseFactory.createResponse(response);
    }

    public void setRequestFactory(HttpUriRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public void setRouter(RequestRouter router) { this.router = router; }

    public void setResponseFactory(JerseyResponseFactory responseFactory) { this.responseFactory = responseFactory; }
}
