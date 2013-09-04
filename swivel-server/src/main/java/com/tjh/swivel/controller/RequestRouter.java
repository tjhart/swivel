package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;
import com.tjh.swivel.model.StubFactory;
import com.tjh.swivel.model.StubRequestHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import vanderbilt.util.Block2;
import vanderbilt.util.PopulatingMap;
import vanderbilt.util.Strings;

import javax.script.ScriptException;
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
    protected final Map<String, List<StubRequestHandler>> stubPaths = new PopulatingMap<String,
            List<StubRequestHandler>>(new ConcurrentHashMap<String, List<StubRequestHandler>>(),
            new Block2<Map<String, List<StubRequestHandler>>, Object, List<StubRequestHandler>>() {
                @Override
                public List<StubRequestHandler> invoke(Map<String, List<StubRequestHandler>> stringListMap, Object o) {
                    return new CopyOnWriteArrayList<StubRequestHandler>();
                }
            });

    private ClientConnectionManager clientConnectionManager;
    private HttpParams httpParams = new BasicHttpParams();
    private StubFactory stubFactory;

    public void setShunt(URI localURI, ShuntRequestHandler requestHandler) {
        shuntPaths.put(localURI.getPath(), requestHandler);
    }

    public void deleteShunt(URI localURI) {
        shuntPaths.remove(localURI.getPath());
    }

    public String addStub(URI localUri, Map<String, Object> stubDescription) throws ScriptException {
        StubRequestHandler stubRequestHandler = stubFactory.createStubFor(localUri, stubDescription);
        stubPaths.get(localUri.getPath()).add(stubRequestHandler);
        return "id";
    }

    public HttpResponse work(HttpRequestBase request) {
        try {
            Deque<String> pathElements = new LinkedList<String>(Arrays.asList(toKeys(request.getURI())));
            ShuntRequestHandler shuntRequestHandler;
            String matchedPath;
            do {
                matchedPath = Strings.join(pathElements.toArray(new String[pathElements.size()]), "/");
                shuntRequestHandler = shuntPaths.get(matchedPath);
                pathElements.removeLast();
            }
            while (shuntRequestHandler == null && !pathElements.isEmpty());
            return shuntRequestHandler.handle(request, new URI(matchedPath), createClient());
        } catch (URISyntaxException use) {
            throw new RuntimeException("Programmer error!", use);
        }
    }

    protected HttpClient createClient() { return new DefaultHttpClient(clientConnectionManager, httpParams); }

    private String[] toKeys(URI localURI) {return localURI.getPath().split("/");}

    public void setClientConnectionManager(ClientConnectionManager clientConnectionManager) {
        this.clientConnectionManager = clientConnectionManager;
    }

    public void setStubFactory(StubFactory stubFactory) { this.stubFactory = stubFactory; }

    public void setHttpParams(HttpParams httpParams) { this.httpParams = httpParams; }
}

class MapPopulator implements Block2<Map<String, Object>, Object, Object> {
    @Override
    public Object invoke(Map<String, Object> stringObjectMap, Object o) {
        return new PopulatingMap<String, Object>(new ConcurrentHashMap<String, Object>(), this);
    }
}