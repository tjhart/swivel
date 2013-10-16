package com.tjh.swivel.model;

import vanderbilt.util.Block2;
import vanderbilt.util.PopulatingMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UriHandlersPopulator implements Block2<Map<String, Map<String, Object>>, Object, Map<String, Object>> {
    private final Block2<Map<String, Object>, Object, Object> handlerMapPopulator = new HandlerMapPopulator();

    @Override
    public Map<String, Object> invoke(Map<String, Map<String, Object>> stringMapMap, Object o) {
        return new PopulatingMap<String, Object>(new ConcurrentHashMap<String, Object>(), handlerMapPopulator);
    }
}
