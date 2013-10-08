package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;
import com.tjh.swivel.model.StubFactory;
import com.tjh.swivel.model.StubRequestHandler;
import org.apache.log4j.Logger;
import vanderbilt.util.Block;
import vanderbilt.util.Block2;
import vanderbilt.util.Lists;
import vanderbilt.util.Maps;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Path("config")
public class ConfigurationResource {
    public static final String STUB_ID_KEY = "id";
    public static final String SHUNT_KEY = "shunt";

    protected static Logger LOGGER = Logger.getLogger(ConfigurationResource.class);
    protected RequestRouter router;
    protected StubFactory stubFactory;

    @POST
    @Path("stub/{localPath: .*}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> postStub(@PathParam("localPath") String localPath, Map<String, Object> stubDescription,
            @Context HttpServletRequest request) throws URISyntaxException, ScriptException {
        StringBuilder sb = new StringBuilder(localPath);
        String queryString = trimToNull(request.getQueryString());
        if (queryString != null) {
            sb.append("?")
                    .append(queryString);
        }
        URI localUri = new URI(sb.toString());
        StubRequestHandler stubRequestHandler = stubFactory.createStubFor(localUri, stubDescription);

        LOGGER.debug(String.format("Adding stub for %1$s", localUri));
        router.addStub(localUri, stubRequestHandler);
        return Maps.<String, Object>asMap(STUB_ID_KEY, stubRequestHandler.getId());
    }

    @DELETE
    @Path("stub/{localPath: .*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Map<String, Object>> deleteStub(@PathParam("localPath") URI localUri,
            @QueryParam(STUB_ID_KEY) int stubId) {
        LOGGER.debug(String.format("Deleting stub with id %1$d at path %2$s", stubId, localUri));
        router.removeStub(localUri, stubId);

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

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Map<String, Object>> reset(){
        LOGGER.debug("Resetting Swivel");
        router.reset();

        return getConfiguration();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
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

    protected static String trimToNull(String source) {
        if (source == null) return null;

        String result = source.trim();
        if (result.length() == 0) {
            result = null;
        }

        return result;
    }

    public void setRouter(RequestRouter router) { this.router = router; }

    public void setStubFactory(StubFactory stubFactory) { this.stubFactory = stubFactory; }
}
