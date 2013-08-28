package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;
import vanderbilt.util.Block2;
import vanderbilt.util.MapNavigator;
import vanderbilt.util.Maps;
import vanderbilt.util.PopulatingMap;

import java.net.URI;
import java.util.Map;

public class RequestRouter {
    //perpetually populating map
    protected final Map<String, Object> shuntPaths =
            new PopulatingMap<String, Object>(new Block2<Map<String, Object>, Object, Object>() {
                @Override
                public Object invoke(Map<String, Object> stringObjectMap, Object o) {
                    return new PopulatingMap<String, Object>(this);
                }
            });

    public void setShunt(URI localURI, ShuntRequestHandler requestHandler) {
        Maps.putValueFor(shuntPaths, requestHandler, toKeys(localURI));
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

    private String[] toKeys(URI localURI) {return localURI.getPath().split("/");}
}
