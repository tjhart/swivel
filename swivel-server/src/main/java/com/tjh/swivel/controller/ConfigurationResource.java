package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Path("config")
public class ConfigurationResource {
    public static final String REMOTE_URI_KEY = "remoteURI";
    protected RequestRouter router;

    //REDTAG:TJH - will the local path accept many path elements?
    @PUT
    @Path("shunt/{localPath: .*}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putShunt(@PathParam("localPath") String localPath, Map<String, String> json) throws URISyntaxException {

        router.setShunt(new URI(localPath), new ShuntRequestHandler(new URI(json.get(REMOTE_URI_KEY))));
    }

    @DELETE
    @Path("shunt/{localPath: .*}")
    public void deleteShunt(@PathParam("localPath") String localPath) throws URISyntaxException {
        router.deleteShunt(new URI(localPath));
    }

    public void setRouter(RequestRouter router) { this.router = router; }
}
