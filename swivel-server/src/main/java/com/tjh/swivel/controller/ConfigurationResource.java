package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;
import org.apache.log4j.Logger;

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

    protected Logger logger = Logger.getLogger(ConfigurationResource.class);
    protected RequestRouter router;

    //REDTAG:TJH - will the local path accept many path elements?
    @PUT
    @Path("shunt/{localPath: .*}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putShunt(@PathParam("localPath") String localPath, Map<String, String> json) throws URISyntaxException {
        String remoteURL = json.get(REMOTE_URI_KEY);
        logger.debug(String.format("Configuring shunt: proxying %1$s to %2$s", localPath, remoteURL));

        router.setShunt(new URI(localPath), new ShuntRequestHandler(new URI(remoteURL)));
    }

    @DELETE
    @Path("shunt/{localPath: .*}")
    public void deleteShunt(@PathParam("localPath") String localPath) throws URISyntaxException {
        logger.debug(String.format("Removing shunt for %1$s", localPath));

        router.deleteShunt(new URI(localPath));
    }

    public void setRouter(RequestRouter router) { this.router = router; }
}
