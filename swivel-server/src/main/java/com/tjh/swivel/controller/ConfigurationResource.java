package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;
import com.tjh.swivel.model.StubRequestHandler;
import com.tjh.swivel.utils.MapToJSONConverter;
import org.apache.log4j.Logger;
import vanderbilt.util.Block;
import vanderbilt.util.Block2;
import vanderbilt.util.Lists;
import vanderbilt.util.Maps;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Path("config")
public class ConfigurationResource {
    public static final String SHUNT_KEY = "shunt";

    protected static Logger LOGGER = Logger.getLogger(ConfigurationResource.class);
    private final MapToJSONConverter mapToJSONConverter = new MapToJSONConverter();
    protected RequestRouter router;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfigurationResponse() {
        return Response.ok()
                .type(MediaType.APPLICATION_JSON)
                .header("Content-Disposition", "attachment; filename=\"swivelConfig.json\"")
                .entity(mapToJSONConverter.toJSON(getConfiguration()))
                .build();
    }

    public Map<String, Map<String, Object>> getConfiguration() {
        Map<String, Map<String, Object>> result = router.getUriHandlers();
        Maps.eachPair(result, new Block2<String, Map<String, Object>, Object>() {
            @SuppressWarnings("unchecked")
            @Override
            public Object invoke(String key, Map<String, Object> handlerMap) {
                if (handlerMap.containsKey(RequestRouter.SHUNT_NODE)) {
                    ShuntRequestHandler shuntRequestHandler =
                            (ShuntRequestHandler) handlerMap.remove(RequestRouter.SHUNT_NODE);
                    handlerMap.put(SHUNT_KEY, shuntRequestHandler.toMap());
                }
                if (handlerMap.containsKey(RequestRouter.STUB_NODE)) {
                    Collection<Map<String, Object>> stubDescriptions =
                            Lists.collect((List<StubRequestHandler>) handlerMap.remove(RequestRouter.STUB_NODE),
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
        router.reset();

        return getConfiguration();
    }

    @DELETE
    @Path("path/{localPath: .*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Map<String, Object>> deletePath(@PathParam("localPath") URI localUri) {
        LOGGER.debug(String.format("Deleting path %1$s", localUri));
        router.removePath(localUri);

        return getConfiguration();
    }

    public void setRouter(RequestRouter router) { this.router = router; }
}
