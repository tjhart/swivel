package com.tjh.swivel.controller;

import org.apache.http.client.methods.HttpRequestBase;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;

@Path("proxy")
public class ProxyResource {
    private RequestRouter router;
    private HttpUriRequestFactory requestFactory;
    private JerseyResponseFactory responseFactory;

    @GET
    @Path("{localPath: .*}")
    public Response get(@PathParam("localPath") URI localPath, @Context HttpServletRequest request) throws IOException {
        return respondTo(requestFactory.createGetRequest(localPath, request));
    }

    @DELETE
    @Path("{localPath: .*}")
    public Response delete(@PathParam("localPath") URI localPath, @Context HttpServletRequest request)
            throws IOException {
        return respondTo(requestFactory.createDeleteRequest(localPath, request));
    }

    @PUT
    @Path("{localPath: .*}")
    public Response put(@PathParam("localPath") URI localPath, String body, @Context HttpServletRequest request)
            throws IOException {
        return respondTo(requestFactory.createPutRequest(localPath, request, body));
    }

    @POST
    @Path("{localPath: .*}")
    public Response post(@PathParam("localPath") URI localPath, String body, @Context HttpServletRequest request)
            throws IOException {
        return respondTo(requestFactory.createPostRequest(localPath, request, body));
    }

    protected Response respondTo(HttpRequestBase request) throws IOException {
        return responseFactory.createResponse(router.route(request));
    }

    public void setRequestFactory(HttpUriRequestFactory requestFactory) { this.requestFactory = requestFactory; }

    public void setRouter(RequestRouter router) { this.router = router; }

    public void setResponseFactory(JerseyResponseFactory responseFactory) { this.responseFactory = responseFactory; }
}
