package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;
import vanderbilt.util.Block2;
import vanderbilt.util.Maps;
import vanderbilt.util.PopulatingMap;

import java.net.URI;
import java.util.Deque;
import java.util.LinkedList;
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
        String[] keys = toKeys(localURI);
        String lastKey = keys[keys.length - 1];
        Deque<Map<String, Object>> branchStack = new LinkedList<Map<String, Object>>();
        Deque<String> keyStack = new LinkedList<String>();
        Map<String, Object> branch = shuntPaths;
        for (int i = 0; i < keys.length - 1; i++) {
            String key = keys[i];

            keyStack.push(key);
            branchStack.push(branch);
            branch = (Map<String, Object>) branch.get(key);
        }

        branch.remove(lastKey);
        while (branch.isEmpty() && !branchStack.isEmpty()) {
            branch = branchStack.pop();
            String pop = keyStack.pop();
            branch.remove(pop);
        }
    }

    private String[] toKeys(URI localURI) {return localURI.getPath().split("/");}
}
