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
import vanderbilt.util.Lists;
import vanderbilt.util.MapNavigator;
import vanderbilt.util.Maps;
import vanderbilt.util.PopulatingMap;
import vanderbilt.util.Strings;

import javax.script.ScriptException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestRouter {

    private Logger logger = Logger.getLogger(RequestRouter.class);
    //perpetually populating map
    protected final Map<String, Object> shuntPaths =
            new PopulatingMap<String, Object>(new ConcurrentHashMap<String, Object>(), new MapPopulator());
    protected final Map<String, Object> stubPaths =
            new PopulatingMap<String, Object>(new ConcurrentHashMap<String, Object>(), new MapPopulator());
    private ClientConnectionManager clientConnectionManager;
    private HttpParams httpParams = new BasicHttpParams();
    private StubFactory stubFactory;

    public void setShunt(URI localURI, ShuntRequestHandler requestHandler) {
        try {
            List<String> keyList = Lists.asList(toKeys(localURI));
            String lastKey = keyList.remove(keyList.size() - 1);
            Map<String, Object> lastBranch = Maps.valueFor(shuntPaths, keyList);
            if (lastBranch.containsKey(lastKey) && lastBranch.get(lastKey) instanceof Map) {
                Object target = lastBranch.get(lastKey);
                logger.warn(String.format("Attempted to set a shunt at %1$s, but that path is used for other shunts: " +
                        "%2$s", localURI, target));
                throw new IllegalArgumentException(
                        String.format("%1$s is unavailable: :%2$s", localURI, target));
            }
            lastBranch.put(lastKey, requestHandler);
        } catch (ClassCastException e) {
            logger.warn(String.format("Attempted to set a shunt at %1$s, but a shunt already exists along that path",
                    localURI));
            throw new IllegalArgumentException(String.format("%1$s is unavailable", localURI), e);
        }
    }

    public void deleteShunt(URI localURI) {
        MapNavigator<String> mapNavigator = new MapNavigator<String>(shuntPaths);
        String[] keys = toKeys(localURI);
        mapNavigator.navigateTo(keys);
        final String[] lastKey = new String[]{keys[keys.length - 1]};
        mapNavigator.unwind(new Block2<String, Object, Boolean>() {
            @Override
            public Boolean invoke(String key, Object val) {
                Map branch = (Map) val;
                branch.remove(lastKey[0]);
                lastKey[0] = key;
                return !branch.isEmpty();
            }
        });
    }

    public String addStub(URI localUri, Map<String, Object> stubDescription) throws ScriptException {
        int identityHashCode = System.identityHashCode(stubDescription);
        System.out.println("identityHashCode = " + identityHashCode);

        StubRequestHandler stubFor = stubFactory.createStubFor(localUri, stubDescription);
        return "id";
    }

    public HttpResponse work(HttpRequestBase request) {
        try {
            MapNavigator<String> navigator = new MapNavigator<String>(shuntPaths);

            final List<String> matchedPath = new ArrayList<String>();
            ShuntRequestHandler shuntRequestHandler =
                    (ShuntRequestHandler) navigator.navigateTo(new Block2<String, Object, Boolean>() {
                        @Override
                        public Boolean invoke(String s, Object o) {
                            matchedPath.add(s);
                            return !(o instanceof Map);
                        }
                    }, toKeys(request.getURI()));

            return shuntRequestHandler.handle(request,
                    new URI(Strings.join("/", matchedPath.toArray(new String[matchedPath.size()]))), createClient());
        } catch (ClassCastException e) {
            deleteShunt(request.getURI());
            throw e;
        } catch (URISyntaxException e) {
            throw new RuntimeException("Programmer error!", e);
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