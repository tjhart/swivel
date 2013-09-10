package com.tjh.swivel.controller;

import com.tjh.swivel.model.RequestHandler;
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

    public static final String SHUNT_NODE = "^shunt";
    public static final String STUB_NODE = "^stub";
    private Logger logger = Logger.getLogger(RequestRouter.class);

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

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public HttpResponse route(HttpRequestBase request) {
        RequestHandler result = null;
        Deque<String> pathElements = new LinkedList<String>(Arrays.asList(toKeys(request.getURI())));
        String matchedPath;
        do {
            matchedPath = Strings.join(pathElements.toArray(new String[pathElements.size()]), "/");
            if (uriHandlers.containsKey(matchedPath)) {
                Map<String, Object> stringObjectMap = uriHandlers.get(matchedPath);
                result = findStub(stringObjectMap, request);
                if (result == null && stringObjectMap.containsKey(SHUNT_NODE)) {
                    result = (RequestHandler) stringObjectMap.get(SHUNT_NODE);
                }
            }
            pathElements.removeLast();
        }
        while (result == null && !pathElements.isEmpty());
        logger.debug(String.format("Routing <%1$s> to <%2$s>. Matched path is: <%3$s>", request, result, matchedPath));

        return result.handle(request, URI.create(matchedPath), createClient());
    }

    @SuppressWarnings("unchecked")
    private RequestHandler findStub(Map<String, Object> stringObjectMap, final HttpUriRequest request) {
        return Lists.find((Collection<StubRequestHandler>) stringObjectMap.get(STUB_NODE),
                new Block<StubRequestHandler, Boolean>() {
                    @Override
                    public Boolean invoke(StubRequestHandler requestHandler) {
                        return requestHandler.matches(request);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public void removeStub(URI localUri, final int stubHandlerId) {
        List<StubRequestHandler> handlers = (List<StubRequestHandler>) uriHandlers
                .get(localUri.getPath())
                .get(STUB_NODE);
        StubRequestHandler target = Lists.find(handlers, new Block<StubRequestHandler, Boolean>() {
            @Override
            public Boolean invoke(StubRequestHandler stubRequestHandler) {
                return stubRequestHandler.getId() == stubHandlerId;
            }
        });
        logger.debug(String.format("Removing <%1$s> from path <%2$s>", target, localUri));
        handlers.remove(target);
    }

    public void setShunt(URI localURI, ShuntRequestHandler requestHandler) {
        logger.debug(String.format("Setting shunt <%1$s> at <%2$s>", requestHandler, localURI));
        uriHandlers.get(localURI.getPath()).put(SHUNT_NODE, requestHandler);
    }

    @SuppressWarnings("unchecked")
    public void addStub(URI localUri, StubRequestHandler stubRequestHandler) {
        logger.debug(String.format("Adding stub <%1$s> to <%2$s>", stubRequestHandler, localUri));
        ((List) uriHandlers.get(localUri.getPath())
                .get(STUB_NODE))
                .add(stubRequestHandler);
    }

    public void deleteShunt(URI localURI) {
        ShuntRequestHandler shuntRequestHandler = (ShuntRequestHandler) uriHandlers.get(localURI.getPath())
                .remove(SHUNT_NODE);

        logger.debug(String.format("Removed <%1$s> from <%2$s>", shuntRequestHandler, localURI));
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

    public void setHttpParams(HttpParams httpParams) { this.httpParams = httpParams; }
    //</editor-fold>
}
