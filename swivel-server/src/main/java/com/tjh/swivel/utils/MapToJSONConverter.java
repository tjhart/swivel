package com.tjh.swivel.utils;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import vanderbilt.util.Block;
import vanderbilt.util.Lists;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MapToJSONConverter implements Block<Object, Object> {
    public JSONObject toJSON(Map<String, ?> stringObjectMap) {
        Map<String, Object> result = new HashMap<String, Object>(stringObjectMap.size());
        for (String key : stringObjectMap.keySet()) {
            Object value = stringObjectMap.get(key);
            if (value instanceof Collection) {
                result.put(key, toJSON((Collection) value));
            } else if (value instanceof Map) {
                result.put(key, toJSON((Map<String, Object>) value));
            } else {
                result.put(key, value);
            }
        }

        return new JSONObject(result);
    }

    @Override
    public Object invoke(Object object) {
        Object result = object;
        if (object instanceof Map)
            result = toJSON((Map<String, Object>) object);
        else if (object instanceof Collection) {
            result = toJSON((Collection) object);
        }

        return result;
    }

    public JSONArray toJSON(Collection collection) {
        return new JSONArray(Lists.collect(collection, this));
    }
}
