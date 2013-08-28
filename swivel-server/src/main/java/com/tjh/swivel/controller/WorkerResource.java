package com.tjh.swivel.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.xml.ws.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Path("proxy")
public class WorkerResource {
    private RequestRouter router;
    private HttpUriRequestFactory requestFactory;

    @GET
    @Path("work/{localPath: .*}")
    public Response get(@PathParam("localPath") String localPath, @Context HttpServletRequest request)
            throws URISyntaxException {
        return router.work(requestFactory.createGetRequest(new URI(localPath), request));
    }

    public void setRequestFactory(HttpUriRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public void setRouter(RequestRouter router) { this.router = router; }
}
