package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;
import org.apache.log4j.Logger;
import vanderbilt.util.Maps;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Path("config")
public class ConfigurationResource {
    public static final String REMOTE_URI_KEY = "remoteURI";

    protected Logger logger = Logger.getLogger(ConfigurationResource.class);
    protected RequestRouter router;

    @PUT
    @Path("shunt/{localPath: .*}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putShunt(@PathParam("localPath") URI localPath, Map<String, String> json)
            throws URISyntaxException {
        try {
            String remoteURL = json.get(REMOTE_URI_KEY);
            logger.debug(String.format("Configuring shunt: proxying %1$s to %2$s", localPath, remoteURL));

            router.setShunt(localPath, new ShuntRequestHandler(new URI(remoteURL)));
            return Response.ok().build();
        } catch (IllegalArgumentException iae) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(iae.getMessage())
                    .build();
        } catch (ClassCastException cce) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(cce.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("shunt/{localPath: .*}")
    public void deleteShunt(@PathParam("localPath") URI localPath) {
        logger.debug(String.format("Removing shunt for %1$s", localPath));

        router.deleteShunt(localPath);
    }

    @PUT
    @Path("stub/{localPath: .*}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> putStub(@PathParam("localPath") String localPath, Map<String, Object> json,
            @Context HttpServletRequest request) throws URISyntaxException {
        StringBuilder sb = new StringBuilder(localPath);
        String queryString = trimToNull(request.getQueryString());
        if (queryString != null) {
            sb.append("?")
                    .append(queryString);
        }
        URI localUri = new URI(sb.toString());

        logger.debug(String.format("Adding stub for %1$s", localUri));
        return Maps.<String, Object>asMap("id", router.addStub(localUri, json));
    }

    protected static String trimToNull(String source) {
        if (source == null) return null;

        String result = source.trim();
        if (result.length() == 0) {
            result = null;
        }

        return result;
    }

    public void setRouter(RequestRouter router) { this.router = router; }
}
