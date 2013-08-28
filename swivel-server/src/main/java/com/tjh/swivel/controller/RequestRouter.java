package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;
import vanderbilt.util.Block2;
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

    public void setShunt(URI localPath, ShuntRequestHandler requestHandler) {
        Maps.putValueFor(shuntPaths, requestHandler, localPath.getPath().split("/"));
    }
}
