package com.tjh.swivel.controller;

import com.tjh.swivel.model.RequestHandler;
import com.tjh.swivel.model.ResponseFactory;
import com.tjh.swivel.model.ShuntRequestHandler;
import com.tjh.swivel.model.StubRequestHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import vanderbilt.util.Block;
import vanderbilt.util.Block2;
import vanderbilt.util.Lists;
import vanderbilt.util.Maps;
import vanderbilt.util.PopulatingMap;
import vanderbilt.util.Strings;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RequestRouter {

    private static Logger LOGGER = Logger.getLogger(RequestRouter.class);

    public static final String SHUNT_NODE = "^shunt";
    public static final String STUB_NODE = "^stub";

    protected final Map<String, Map<String, Object>> uriHandlers = new PopulatingMap<String, Map<String, Object>>(
            new ConcurrentHashMap<String, Map<String, Object>>(),
            new Block2<Map<String, Map<String, Object>>, Object, Map<String, Object>>() {
                @Override
                public Map<String, Object> invoke(Map<String, Map<String, Object>> stringMapMap, Object o) {
                    return new PopulatingMap<String, Object>(new ConcurrentHashMap<String, Object>(),
                            new Block2<Map<String, Object>, Object, Object>() {
                                @Override
                                public Object invoke(Map<String, Object> stringObjectMap, Object o) {
                                    Object result = null;
                                    if (STUB_NODE.equals(o)) {
                                        result = new CopyOnWriteArrayList();
                                    }

                                    return result;
                                }
                            });
                }
            }
    );

    private ClientConnectionManager clientConnectionManager;
    private HttpParams httpParams = new BasicHttpParams();
    private ResponseFactory responseFactory;

    public HttpResponse route(HttpRequestBase request) {
        LOGGER.debug("Routing " + request);
        RequestHandler requestHandler = null;
        Deque<String> pathElements = new LinkedList<String>(Arrays.asList(toKeys(request.getURI())));
        String matchedPath;
        do {
            matchedPath = Strings.join(pathElements.toArray(new String[pathElements.size()]), "/");
            requestHandler = findRequestHandler(request, matchedPath);
            pathElements.removeLast();
        }
        while (requestHandler == null && !pathElements.isEmpty());

        return createResponse(request, requestHandler, matchedPath);
    }

    @SuppressWarnings("unchecked")
    private HttpResponse createResponse(HttpRequestBase request, RequestHandler requestHandler, String matchedPath) {
        HttpResponse response;
        if (requestHandler == null) {
            LOGGER.debug("No handler found");
            response = responseFactory.createResponse(404,
                    "Not Found: Swivel found no handlers along path " + request.getURI());
        } else {

            LOGGER.debug(String.format("Routing <%1$s> to <%2$s>. Matched path is: <%3$s>", request, requestHandler,
                    matchedPath));
            response = requestHandler.handle(request, URI.create(matchedPath), createClient());
        }
        return response;
    }

    private RequestHandler findRequestHandler(HttpRequestBase request, String matchedPath) {
        RequestHandler result = null;
        LOGGER.debug("checking for handlers at " + matchedPath);
        if (uriHandlers.containsKey(matchedPath)) {
            Map<String, Object> stringObjectMap = uriHandlers.get(matchedPath);
            result = findStub(stringObjectMap, request);
            if (result == null && stringObjectMap.containsKey(SHUNT_NODE)) {
                result = (RequestHandler) stringObjectMap.get(SHUNT_NODE);
                if (result != null) {
                    LOGGER.debug("found shunt " + result);
                }
            } else if (result != null) {
                LOGGER.debug("found stub " + result);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    RequestHandler findStub(Map<String, Object> stringObjectMap, final HttpUriRequest request) {
        return Lists.find((Collection<StubRequestHandler>) stringObjectMap.get(STUB_NODE),
                new Block<StubRequestHandler, Boolean>() {
                    @Override
                    public Boolean invoke(StubRequestHandler requestHandler) {
                        if (Level.DEBUG.equals(LOGGER.getEffectiveLevel())) {
                            LOGGER.debug("Checking Stub " + requestHandler);
                        }
                        return requestHandler.matches(request);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public void removeStub(URI localUri, final int stubHandlerId) {
        String path = localUri.getPath();
        Map<String, Object> handlerMap = uriHandlers.get(path);
        List<StubRequestHandler> handlers = (List<StubRequestHandler>) handlerMap
                .get(STUB_NODE);
        StubRequestHandler target = Lists.find(handlers, new Block<StubRequestHandler, Boolean>() {
            @Override
            public Boolean invoke(StubRequestHandler stubRequestHandler) {
                return stubRequestHandler.getId() == stubHandlerId;
            }
        });
        LOGGER.debug(String.format("Removing <%1$s> from path <%2$s>", target, localUri));
        handlers.remove(target);

        if (handlers.isEmpty()) {
            clean(path, handlerMap, STUB_NODE);
        }
    }

    public void setShunt(URI localURI, ShuntRequestHandler requestHandler) {
        LOGGER.debug(String.format("Setting shunt <%1$s> at <%2$s>", requestHandler, localURI));
        uriHandlers.get(localURI.getPath()).put(SHUNT_NODE, requestHandler);
    }

    @SuppressWarnings("unchecked")
    public void addStub(URI localUri, StubRequestHandler stubRequestHandler) {
        LOGGER.debug(String.format("Adding stub <%1$s> to <%2$s>", stubRequestHandler, localUri));
        ((List) uriHandlers.get(localUri.getPath())
                .get(STUB_NODE))
                .add(0, stubRequestHandler);
    }

    public void deleteShunt(URI localURI) {
        String path = localURI.getPath();
        clean(path, uriHandlers.get(path), SHUNT_NODE);
    }

    private void clean(String path, Map<String, Object> handlerMap, String nodeType) {
        Object shuntRequestHandler = handlerMap.remove(nodeType);
        if (handlerMap.isEmpty()) {
            uriHandlers.remove(path);
        }

        LOGGER.debug(String.format("Removed <%1$s> from <%2$s>", shuntRequestHandler, path));
    }

    protected HttpClient createClient() { return new DefaultHttpClient(clientConnectionManager, httpParams); }

    private String[] toKeys(URI localURI) {return localURI.getPath().split("/");}

    //<editor-fold desc="bean">
    public Map<String, Map<String, Object>> getUriHandlers() {
        final HashMap<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>(uriHandlers);
        Maps.eachPair(result, new Block2<String, Map<String, Object>, Object>() {
            @Override
            public Object invoke(String s, Map<String, Object> stringObjectMap) {
                result.put(s, new HashMap<String, Object>(stringObjectMap));
                return null;
            }
        });
        return result;
    }

    public void setClientConnectionManager(ClientConnectionManager clientConnectionManager) {
        this.clientConnectionManager = clientConnectionManager;
    }

    public void setResponseFactory(ResponseFactory responseFactory) { this.responseFactory = responseFactory; }

    public void setHttpParams(HttpParams httpParams) { this.httpParams = httpParams; }
    //</editor-fold>
}
