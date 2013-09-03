package com.tjh.swivel.model;

import org.hamcrest.Matcher;
import vanderbilt.util.PopulatingMap;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.tjh.swivel.model.matchers.ContentMatcher.hasContent;
import static com.tjh.swivel.model.matchers.ContentTypeMatcher.hasContentType;
import static com.tjh.swivel.model.matchers.HeaderMatcher.hasHeader;
import static com.tjh.swivel.model.matchers.ParameterMapMatcher.hasParameterMap;
import static com.tjh.swivel.model.matchers.RemoteAddrMatcher.hasRemoteAddr;
import static com.tjh.swivel.model.matchers.RequestMethodMatcher.hasMethod;
import static com.tjh.swivel.model.matchers.RequestedURIPathMatcher.hasURIPath;
import static com.tjh.swivel.model.matchers.ScriptMatcher.scriptMatches;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;

/**
 * These rules were build quickly and naively. They're not indexable or inspectable in any way.
 * The matchers are great for evaluation, though. we need to find a way to keep the encapsulation,
 * but organize the matchers to be inspectable and/or the rules indexable
 */
public class MatcherFactory {
    public static final String METHOD_KEY = "method";
    public static final String REMOTE_ADDR_KEY = "remoteAddr";
    public static final String CONTENT_TYPE_KEY = "contentType";
    public static final String CONTENT_KEY = "content";
    public static final String SCRIPT_KEY = "when";
    public static final int STATIC_MATCHER_COUNT = 3;
    public static final int OPTIONAL_MATCHER_COUNT = 5;

    /**
     * these rules are not ready for prime time. It was a place to get started for the core of the
     * functionality, but will change drastically as the controller layer gets build
     */
    @SuppressWarnings("unchecked")
    public Matcher<HttpServletRequest> buildMatcher(URI localURI, Map<String, String> stubDescription) {

        List<Matcher<HttpServletRequest>> matchers = new ArrayList<Matcher<HttpServletRequest>>(STATIC_MATCHER_COUNT);
        //YELLOWTAG;TJH what about 'any' method?
        matchers.add(hasMethod(equalTo(stubDescription.get(METHOD_KEY))));
        //YELLOWTAG:TJH - what about rest path trees? Should this be 'startsWith?'
        matchers.add(hasURIPath(equalTo(localURI.toString())));

        matchers.add(buildOptionalMatcher(localURI, stubDescription));
        return allOf(matchers.toArray(new Matcher[matchers.size()]));
    }

    @SuppressWarnings("unchecked")
    protected Matcher<HttpServletRequest> buildOptionalMatcher(URI localURI, Map<String, String> stubDescription) {
        try {
            List<Matcher<HttpServletRequest>> result =
                    new ArrayList<Matcher<HttpServletRequest>>(OPTIONAL_MATCHER_COUNT);
            Map<String, List<String>> queryParameters = convertParameterMap(localURI.getQuery());
            if (!queryParameters.isEmpty()) {
                result.add(hasParameterMap(equalTo(queryParameters)));
            }
            if (stubDescription.containsKey(REMOTE_ADDR_KEY)) {
                String remoteAddr = stubDescription.get(REMOTE_ADDR_KEY);
                result.add(
                        anyOf(hasRemoteAddr(equalTo(remoteAddr)), hasHeader("X-Forwarded-For", hasItem(remoteAddr))));
            }
            if (stubDescription.containsKey(CONTENT_TYPE_KEY)) {
                result.add(hasContentType(equalTo(stubDescription.get(CONTENT_TYPE_KEY))));
            }
            if (stubDescription.containsKey(CONTENT_KEY)) {
                result.add(hasContent(equalTo(stubDescription.get(CONTENT_KEY))));
            }
            if (stubDescription.containsKey(SCRIPT_KEY)) {
                result.add(scriptMatches(stubDescription.get(SCRIPT_KEY)));
            }
            return allOf(result.toArray(new Matcher[result.size()]));
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, List<String>> convertParameterMap(String queryString) {
        if (queryString == null) return Collections.emptyMap();
        try {
            Constructor constructor = ArrayList.class.getConstructor();
            Map<String, List<String>> result =
                    new PopulatingMap<String, List<String>>(constructor);
            String[] entries = queryString.split("\\&");
            for (String entry : entries) {
                String[] keyVal = entry.split("=");
                result.get(keyVal[0]).add(keyVal[1]);
            }
            return result;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Programmer error", e);
        }
    }
}
