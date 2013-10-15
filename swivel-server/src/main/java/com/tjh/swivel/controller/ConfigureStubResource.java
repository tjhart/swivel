package com.tjh.swivel.controller;

import com.tjh.swivel.model.Configuration;
import com.tjh.swivel.model.StubFactory;
import com.tjh.swivel.model.StubRequestHandler;
import org.apache.log4j.Logger;
import vanderbilt.util.Block;
import vanderbilt.util.Lists;
import vanderbilt.util.Maps;

import javax.script.ScriptException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Path("config/stub/{localPath: .*}")
public class ConfigureStubResource {
    public static final String STUB_ID_KEY = "id";
    protected static Logger LOGGER = Logger.getLogger(ConfigureStubResource.class);

    protected StubFactory stubFactory;
    protected ConfigurationResource configurationResource;
    private Configuration configuration;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Map<String, Object>> getStub(@PathParam("localPath") String localPath,
            @QueryParam("ids[]") List<Integer> stubIds) {
        return Lists.collect(configuration.getStubs(localPath, stubIds),
                new Block<StubRequestHandler, Map<String, Object>>() {
                    @Override
                    public Map<String, Object> invoke(StubRequestHandler stubRequestHandler) {
                        return stubRequestHandler.toMap();
                    }
                });
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> postStub(@PathParam("localPath") String localPath, Map<String, Object> stubDescription)
            throws URISyntaxException, ScriptException {
        URI localUri = new URI(localPath);
        StubRequestHandler stubRequestHandler = stubFactory.createStubFor(localUri, stubDescription);

        LOGGER.debug(String.format("Adding stub for %1$s", localUri));
        configuration.addStub(localUri, stubRequestHandler);
        return Maps.<String, Object>asMap(STUB_ID_KEY, stubRequestHandler.getId());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> editStub(@PathParam("localPath") String localPath, Map<String, Object> stubDescription)
            throws URISyntaxException, ScriptException {

        int lastElementIndex = localPath.lastIndexOf('/');
        int stubId = Integer.valueOf(localPath.substring(lastElementIndex + 1));
        localPath = localPath.substring(0, lastElementIndex);

        LOGGER.debug(String.format("Replacing stub %1$s:%2$d with %3$s", localPath, stubId, stubDescription));
        URI localURI = new URI(localPath);
        StubRequestHandler stubRequestHandler = stubFactory.createStubFor(localURI, stubDescription);

        configuration.replaceStub(localURI, stubId, stubRequestHandler);
        return Maps.<String, Object>asMap(STUB_ID_KEY, stubRequestHandler.getId());
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Map<String, Object>> deleteStub(@PathParam("localPath") URI localUri,
            @QueryParam(STUB_ID_KEY) int stubId) {
        LOGGER.debug(String.format("Deleting stub with id %1$d at path %2$s", stubId, localUri));
        configuration.removeStub(localUri, stubId);

        return configurationResource.getConfigurationMap();
    }

    public void setConfigurationResource(ConfigurationResource configurationResource) {
        this.configurationResource = configurationResource;
    }

    public void setStubFactory(StubFactory stubFactory) { this.stubFactory = stubFactory; }

    public void setConfiguration(Configuration configuration) {this.configuration = configuration;}
}
