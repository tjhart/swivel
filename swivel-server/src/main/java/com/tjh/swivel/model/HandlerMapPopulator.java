package com.tjh.swivel.model;

import vanderbilt.util.Block2;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class HandlerMapPopulator implements Block2<Map<String, Object>, Object, Object> {
    @Override
    public Object invoke(Map<String, Object> map, Object o) {
        Object result = null;
        if (Configuration.STUB_NODE.equals(o)) {
            result = new CopyOnWriteArrayList();
        }

        return result;
    }
}
