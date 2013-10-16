package com.tjh.swivel.model;

import vanderbilt.util.Block2;

import java.util.List;
import java.util.Map;

public class HandlerMapPopulator implements Block2<Map<String, Object>, Object, Object> {
    private final Class<? extends List> stubListType;

    public HandlerMapPopulator(Class<? extends List> stubListType) {this.stubListType = stubListType;}

    @Override
    public Object invoke(Map<String, Object> map, Object o) {
        try {
            Object result = null;
            if (Configuration.STUB_NODE.equals(o)) {
                result = stubListType.newInstance();
            }

            return result;

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
