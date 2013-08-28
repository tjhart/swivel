package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import vanderbilt.util.Block2;
import vanderbilt.util.Lists;
import vanderbilt.util.MapNavigator;
import vanderbilt.util.Maps;
import vanderbilt.util.PopulatingMap;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestRouter {
    //perpetually populating map
    protected final Map<String, Object> shuntPaths =
            new PopulatingMap<String, Object>(new ConcurrentHashMap<String, Object>(),
                    new Block2<Map<String, Object>, Object, Object>() {
                        @Override
                        public Object invoke(Map<String, Object> stringObjectMap, Object o) {
                            return new PopulatingMap<String, Object>(this);
                        }
                    });
    private ClientConnectionManager clientConnectionManager;
    private HttpParams httpParams = new BasicHttpParams();

    public void setShunt(URI localURI, ShuntRequestHandler requestHandler) {
        try {
            List<String> keyList = Lists.asList(toKeys(localURI));
            String lastKey = keyList.remove(keyList.size() - 1);
            Map<String, Object> lastBranch = Maps.valueFor(shuntPaths, keyList);
            if (lastBranch.containsKey(lastKey) && lastBranch.get(lastKey) instanceof Map) {
                throw new IllegalArgumentException(
                        String.format("%1$s is unavailable: :%2$s", localURI, lastBranch.get(lastKey)));
            }
            lastBranch.put(lastKey, requestHandler);
        } catch (ClassCastException e) {
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
                return branch.isEmpty();
            }
        });
    }

    public HttpResponse work(HttpUriRequest request) {
        MapNavigator<String> navigator = new MapNavigator<String>(shuntPaths);

        ShuntRequestHandler shuntRequestHandler = navigator.navigateTo(new Block2<String, Object, Boolean>() {
            @Override
            public Boolean invoke(String s, Object o) { return o instanceof Map; }
        }, toKeys(request.getURI()));

        return shuntRequestHandler.handle(request, createClient());
    }

    protected HttpClient createClient() { return new DefaultHttpClient(clientConnectionManager, httpParams); }

    private String[] toKeys(URI localURI) {return localURI.getPath().split("/");}

    public void setClientConnectionManager(ClientConnectionManager clientConnectionManager) {
        this.clientConnectionManager = clientConnectionManager;
    }

    public void setHttpParams(HttpParams httpParams) { this.httpParams = httpParams; }
}
