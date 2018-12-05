package com.tjh.swivel.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URIUtils {

    public static Map<String, List<String>> createMapFromQueryString(String queryString) {
        if (queryString == null) { return Collections.emptyMap(); }
        Map<String, List<String>> result = new HashMap<>();
        for (String entry : queryString.split("\\&")) {
            String[] keyVal = entry.split("=");
            result.computeIfAbsent(keyVal[0], i -> new ArrayList<>()).add(keyVal[1]);
        }
        return result;
    }

    public static URI resolveUri(URI baseUri, URI requestedUri, URI matchedUri) {
        String relativePath = requestedUri
                .getPath()
                .substring(matchedUri.getPath().length());
        String result = baseUri.toString() + relativePath;
        if (requestedUri.getQuery() != null) {
            result += "?" + requestedUri.getQuery();
        }
        return URI.create(result);
    }
}
