package com.tjh.swivel.controller;

import com.sun.jersey.multipart.FormDataParam;
import com.tjh.swivel.model.Configuration;
import com.tjh.swivel.model.ShuntRequestHandler;
import com.tjh.swivel.model.StubRequestHandler;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import vanderbilt.util.Block;
import vanderbilt.util.Block2;
import vanderbilt.util.Lists;
import vanderbilt.util.Maps;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Path("config")
public class ConfigurationResource {
    public static final String SHUNT_KEY = "shunt";

    protected static Logger LOGGER = Logger.getLogger(ConfigurationResource.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Configuration configuration;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfigurationResponse() throws IOException {
        StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, getConfigurationMap());

        return Response.ok()
                .type(MediaType.APPLICATION_JSON)
                .header("Content-Disposition", "attachment; filename=\"swivelConfig.json\"")
                .entity(stringWriter.toString())
                .build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Map<String, Map<String, Object>> putConfiguration(@FormDataParam("swivelConfig") InputStream inputStream)
            throws IOException {
        Map<String, Map<String, Object>> swivelConfig = objectMapper.readValue(inputStream, Map.class);


        return getConfigurationMap();
    }

    public Map<String, Map<String, Object>> getConfigurationMap() {
        Map<String, Map<String, Object>> result = configuration.getUriHandlers();
        Maps.eachPair(result, new Block2<String, Map<String, Object>, Object>() {
            @SuppressWarnings("unchecked")
            @Override
            public Object invoke(String key, Map<String, Object> handlerMap) {
                if (handlerMap.containsKey(Configuration.SHUNT_NODE)) {
                    ShuntRequestHandler shuntRequestHandler =
                            (ShuntRequestHandler) handlerMap.remove(Configuration.SHUNT_NODE);
                    handlerMap.put(SHUNT_KEY, shuntRequestHandler.toMap());
                }
                if (handlerMap.containsKey(Configuration.STUB_NODE)) {
                    Collection<Map<String, Object>> stubDescriptions =
                            Lists.collect((List<StubRequestHandler>) handlerMap.remove(Configuration.STUB_NODE),
                                    new Block<StubRequestHandler, Map<String, Object>>() {
                                        @Override
                                        public Map<String, Object> invoke(StubRequestHandler stubRequestHandler) {
                                            return stubRequestHandler.toMap();
                                        }
                                    });
                    if (!stubDescriptions.isEmpty()) {
                        handlerMap.put("stubs", stubDescriptions);
                    }
                }
                return null;
            }
        });

        return result;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Map<String, Object>> reset() {
        LOGGER.debug("Resetting Swivel");
        configuration.reset();

        return getConfigurationMap();
    }

    @DELETE
    @Path("path/{localPath: .*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Map<String, Object>> deletePath(@PathParam("localPath") URI localUri) {
        LOGGER.debug(String.format("Deleting path %1$s", localUri));
        configuration.removePath(localUri);

        return getConfigurationMap();
    }

    public void setConfiguration(Configuration configuration) {this.configuration = configuration;}
}
