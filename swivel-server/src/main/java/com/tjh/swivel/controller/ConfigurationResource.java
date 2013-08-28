package com.tjh.swivel.controller;

import com.tjh.swivel.model.Shunt;
import com.tjh.swivel.model.ShuntRequestHandler;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Path("config")
public class ConfigurationResource {
    public static final String TARGET_URI_KEY = "targetURI";
    protected RequestRouter router;

    @PUT
    @Path("shunt/{baseTargetPath}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putShunt(@PathParam("baseTargetPath") String baseTargetPath, Map<String, String> json) throws URISyntaxException {

        router.setShunt(new Shunt(new URI(baseTargetPath), new ShuntRequestHandler(new URI(json.get(TARGET_URI_KEY)))));
    }

    public void setRouter(RequestRouter router) { this.router = router; }
}
