package com.tjh.swivel.controller;

import com.sun.jersey.multipart.FormDataParam;
import com.tjh.swivel.model.Configuration;
import com.tjh.swivel.model.ShuntRequestHandler;
import com.tjh.swivel.model.UriHandlersPopulator;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import vanderbilt.util.Block2;
import vanderbilt.util.Maps;
import vanderbilt.util.PopulatingMap;

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
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("config")
public class ConfigurationResource {
    protected static Logger LOGGER = Logger.getLogger(ConfigurationResource.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private Configuration configuration;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfigurationResponse() throws IOException {
        StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter, configuration.toMap());

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

        configuration.load(prepare(objectMapper.readValue(inputStream, Map.class)));
        return configuration.toMap();
    }

    private Map<String, Map<String, Object>> prepare(Map<String, Map<String, Object>> swivelConfig) {
        final Map<String, Map<String, Object>> newConfig = new PopulatingMap<String, Map<String, Object>>(
                new UriHandlersPopulator(HashMap.class, ArrayList.class));
        Maps.eachPair(swivelConfig, new Block2<String, Map<String, Object>, Object>() {
            @Override
            public Object invoke(String uri, Map<String, Object> shuntsAndStubs) {
                Map<String, Object> nodeMap = newConfig.get(uri);
                loadShunt((Map<String, String>) shuntsAndStubs.get(Configuration.SHUNT_MAP_KEY), nodeMap);
                loadStubs((List<Map<String, Object>>) shuntsAndStubs.get(Configuration.STUBS_MAP_KEY), nodeMap);
                return null;
            }
        });

        return newConfig;
    }

    private void loadStubs(List<Map<String, Object>> shuntDescriptions, Map<String, Object> nodeMap) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void loadShunt(Map<String, String> shuntDescription, Map<String, Object> nodeMap) {
        try {
            if (shuntDescription != null) {
                nodeMap.put(Configuration.SHUNT_NODE,
                        new ShuntRequestHandler(shuntDescription));
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Map<String, Object>> reset() {
        LOGGER.debug("Resetting Swivel");
        configuration.reset();

        return configuration.toMap();
    }

    @DELETE
    @Path("path/{localPath: .*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Map<String, Object>> deletePath(@PathParam("localPath") URI localUri) {
        LOGGER.debug(String.format("Deleting path %1$s", localUri));
        configuration.removePath(localUri);

        return configuration.toMap();
    }

    public void setConfiguration(Configuration configuration) {this.configuration = configuration;}

    public void setObjectMapper(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }
}
