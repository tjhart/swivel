package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;
import com.tjh.swivel.model.StubRequestHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import vanderbilt.util.Block;
import vanderbilt.util.Block2;
import vanderbilt.util.Lists;
import vanderbilt.util.PopulatingMap;
import vanderbilt.util.Strings;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RequestRouter {

    private Logger logger = Logger.getLogger(RequestRouter.class);

    protected final Map<String, ShuntRequestHandler> shuntPaths = new ConcurrentHashMap<String, ShuntRequestHandler>();
    protected final Map<String, List<StubRequestHandler>> stubPaths =
            new PopulatingMap<String, List<StubRequestHandler>>(
                    new ConcurrentHashMap<String, List<StubRequestHandler>>(),
                    new Block2<Map<String, List<StubRequestHandler>>, Object, List<StubRequestHandler>>() {
                        @Override
                        public List<StubRequestHandler> invoke(Map<String, List<StubRequestHandler>> stringListMap,
                                Object o) {
                            return new CopyOnWriteArrayList<StubRequestHandler>();
                        }
                    });

    private ClientConnectionManager clientConnectionManager;
    private HttpParams httpParams = new BasicHttpParams();

    public HttpResponse work(HttpRequestBase request) {
        //YELLOWTAG:TJH - would it be better to have stubs and shunts in the same map?
        //might be more efficient and less surprising than a matching stub at '/foo'
        //when a shunt existed at '/foo/bar'
        HttpResponse result = stub(request);
        if (result == null) {
            result = shunt(request);
        }
        return result;
    }

    protected HttpResponse stub(final HttpRequestBase request) {
        try {
            final String[] matchedPaths = new String[]{null};
            StubRequestHandler handler =
                    findHandler(request, stubPaths, new Block2<List<StubRequestHandler>, String, StubRequestHandler>() {
                        @Override
                        public StubRequestHandler invoke(List<StubRequestHandler> stubRequestHandlers,
                                final String matchedPath) {
                            return Lists.find(stubRequestHandlers, new Block<StubRequestHandler, Boolean>() {
                                @Override
                                public Boolean invoke(StubRequestHandler stubRequestHandler) {
                                    boolean matches = stubRequestHandler.matches(request);
                                    if (matches) {
                                        matchedPaths[0] = matchedPath;
                                    }
                                    return matches;
                                }
                            });
                        }
                    });
            return handler == null
                    ? null
                    : handler.handle(request, new URI(matchedPaths[0]), createClient());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Programmer Error!", e);
        }
    }

    protected HttpResponse shunt(HttpRequestBase request) {
        try {
            final String[] matchedPaths = new String[]{null};
            ShuntRequestHandler shuntRequestHandler =
                    findHandler(request, shuntPaths, new Block2<ShuntRequestHandler, String, ShuntRequestHandler>() {
                        @Override
                        public ShuntRequestHandler invoke(ShuntRequestHandler shuntRequestHandler, String matchedPath) {
                            matchedPaths[0] = matchedPath;
                            return shuntRequestHandler;
                        }
                    });
            return shuntRequestHandler.handle(request, new URI(matchedPaths[0]), createClient());
        } catch (URISyntaxException use) {
            throw new RuntimeException("Programmer error!", use);
        }
    }

    protected <T, U> T findHandler(HttpRequestBase request, Map<String, U> handlerMap, Block2<U, String, T> block) {
        T result = null;
        Deque<String> pathElements = new LinkedList<String>(Arrays.asList(toKeys(request.getURI())));
        String matchedPath;
        do {
            matchedPath = Strings.join(pathElements.toArray(new String[pathElements.size()]), "/");
            U u = handlerMap.get(matchedPath);
            if (u != null) {
                result = block.invoke(u, matchedPath);
            }
            pathElements.removeLast();
        }
        while (result == null && !pathElements.isEmpty());

        return result;
    }

    public void removeStub(URI localUri, final int stubHandlerId) {
        List<StubRequestHandler> handlers = stubPaths.get(localUri.getPath());
        StubRequestHandler target = Lists.find(handlers, new Block<StubRequestHandler, Boolean>() {
            @Override
            public Boolean invoke(StubRequestHandler stubRequestHandler) {
                return stubRequestHandler.getId() == stubHandlerId;
            }
        });
        handlers.remove(target);
    }

    public void setShunt(URI localURI, ShuntRequestHandler requestHandler) {
        shuntPaths.put(localURI.getPath(), requestHandler);
    }

    public void addStub(URI localUri, StubRequestHandler stubRequestHandler) {
        stubPaths.get(localUri.getPath()).add(stubRequestHandler);
    }

    public void deleteShunt(URI localURI) { shuntPaths.remove(localURI.getPath()); }

    protected HttpClient createClient() { return new DefaultHttpClient(clientConnectionManager, httpParams); }

    private String[] toKeys(URI localURI) {return localURI.getPath().split("/");}

    //<editor-fold desc="bean">
    public void setClientConnectionManager(ClientConnectionManager clientConnectionManager) {
        this.clientConnectionManager = clientConnectionManager;
    }

    public void setHttpParams(HttpParams httpParams) { this.httpParams = httpParams; }
    //</editor-fold>
}
