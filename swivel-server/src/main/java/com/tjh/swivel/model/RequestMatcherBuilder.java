package com.tjh.swivel.model;

import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.Matcher;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static com.tjh.swivel.model.matchers.ContentMatcher.hasContent;
import static com.tjh.swivel.model.matchers.ContentTypeMatcher.hasContentType;
import static com.tjh.swivel.model.matchers.HeaderMatcher.hasHeader;
import static com.tjh.swivel.model.matchers.ParameterMapMatcher.hasParameterMap;
import static com.tjh.swivel.model.matchers.RemoteAddrMatcher.hasRemoteAddr;
import static com.tjh.swivel.model.matchers.RequestMethodMatcher.hasMethod;
import static com.tjh.swivel.model.matchers.RequestedURIPathMatcher.hasURIPath;
import static com.tjh.swivel.model.matchers.ScriptMatcher.scriptMatches;

/**
 * These rules were build quickly and naively. They're not indexable or inspectable in any way.
 * The matchers are great for evaluation, though. we need to find a way to keep the encapsulation,
 * but organize the matchers to be inspectable and/or the rules indexable
 */
public class RequestMatcherBuilder {
    public static final String METHOD_KEY = "method";
    public static final String REMOTE_ADDR_KEY = "remoteAddr";
    public static final String CONTENT_TYPE_KEY = "contentType";
    public static final String CONTENT_KEY = "content";
    public static final String SCRIPT_KEY = "when";
    public static final int STATIC_MATCHER_COUNT = 3;
    public static final int OPTIONAL_MATCHER_COUNT = 5;
    protected ObjectMapper objectMapper = new ObjectMapper();

    /**
     * these rules are not ready for prime time. It was a place to get started for the core of the
     * functionality, but will change drastically as the controller layer gets build
     * @param expectationRequest
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public Matcher<HttpServletRequest> buildMatcher(final HttpServletRequest expectationRequest)
            throws IOException {
        Map<String, Object> json = objectMapper.readValue(expectationRequest.getInputStream(), Map.class);

        List<Matcher<HttpServletRequest>> matchers = new ArrayList<Matcher<HttpServletRequest>>(STATIC_MATCHER_COUNT);
        //YELLOWTAG;TJH what about 'any' method?
        matchers.add(hasMethod(equalTo(json.get(METHOD_KEY))));
        //YELLOWTAG:TJH - what about rest path trees? Should this be 'startsWith?'
        matchers.add(hasURIPath(equalTo(expectationRequest.getPathInfo())));

        matchers.add(buildOptionalMatcher(expectationRequest, json));
        return allOf(matchers.toArray(new Matcher[matchers.size()]));
    }

    @SuppressWarnings("unchecked")
    protected Matcher<HttpServletRequest> buildOptionalMatcher(HttpServletRequest expectationRequest,
            Map<String, Object> json) {
        try {
            List<Matcher<HttpServletRequest>> result =
                    new ArrayList<Matcher<HttpServletRequest>>(OPTIONAL_MATCHER_COUNT);
            Map<String, String[]> parameterMap = expectationRequest.getParameterMap();
            if (!parameterMap.isEmpty()) {
                result.add(hasParameterMap(equalTo(convertParameterMap(parameterMap))));
            }
            if (json.containsKey(REMOTE_ADDR_KEY)) {
                String remoteAddr = (String) json.get(REMOTE_ADDR_KEY);
                result.add(
                        anyOf(hasRemoteAddr(equalTo(remoteAddr)), hasHeader("X-Forwarded-For", hasItem(remoteAddr))));
            }
            if (json.containsKey(CONTENT_TYPE_KEY)) {
                result.add(hasContentType(equalTo(json.get(CONTENT_TYPE_KEY))));
            }
            if (json.containsKey(CONTENT_KEY)) {
                result.add(hasContent(equalTo(json.get(CONTENT_KEY))));
            }
            if (json.containsKey(SCRIPT_KEY)) {
                result.add(scriptMatches((String) json.get(SCRIPT_KEY)));
            }
            return allOf(result.toArray(new Matcher[result.size()]));
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, List<String>> convertParameterMap(Map<String, String[]> parameterMap) {
        Map<String, List<String>> result = new HashMap<String, List<String>>(parameterMap.size());
        for (Map.Entry<String, String[]> stringEntry : parameterMap.entrySet()) {
            result.put(stringEntry.getKey(), Arrays.asList(stringEntry.getValue()));
        }
        return result;
    }
}
