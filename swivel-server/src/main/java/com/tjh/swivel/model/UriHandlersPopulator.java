package com.tjh.swivel.model;

import vanderbilt.util.Block2;
import vanderbilt.util.PopulatingMap;

import java.util.List;
import java.util.Map;

public class UriHandlersPopulator implements Block2<Map<String, Map<String, Object>>, Object, Map<String, Object>> {
    private final Class<? extends Map> uriMapType;
    private final Block2<Map<String, Object>, Object, Object> handlerMapPopulator;

    public UriHandlersPopulator(Class<? extends Map> uriMapType, Class<? extends List> stubListType) {
        this.uriMapType = uriMapType;
        this.handlerMapPopulator = new HandlerMapPopulator(stubListType);
    }

    @Override
    public Map<String, Object> invoke(Map<String, Map<String, Object>> stringMapMap, Object o) {
        try {
            return new PopulatingMap<String, Object>(uriMapType.newInstance(), handlerMapPopulator);
        } catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
        } catch (InstantiationException ie) {
            throw new RuntimeException(ie);
        }
    }
}
