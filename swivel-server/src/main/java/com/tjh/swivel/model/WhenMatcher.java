package com.tjh.swivel.model;

import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.tjh.swivel.model.matchers.ContentMatcher.hasContent;
import static com.tjh.swivel.model.matchers.ContentTypeMatcher.hasContentType;
import static com.tjh.swivel.model.matchers.QueryStringMatcher.hasQueryString;
import static com.tjh.swivel.model.matchers.RequestHeaderMatcher.hasHeader;
import static com.tjh.swivel.model.matchers.RequestMethodMatcher.hasMethod;
import static com.tjh.swivel.model.matchers.ScriptMatcher.scriptMatches;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;

public class WhenMatcher implements Matcher<HttpUriRequest> {
    public static final String METHOD_KEY = "method";
    public static final String REMOTE_ADDR_KEY = "remoteAddress";
    public static final String CONTENT_TYPE_KEY = "contentType";
    public static final String CONTENT_KEY = "content";
    public static final String SCRIPT_KEY = "script";
    public static final String QUERY_KEY = "query";
    public static final int OPTIONAL_MATCHER_COUNT = 6;

    private final Matcher<? extends HttpUriRequest> matcher;
    private final Map<String, String> when;

    public WhenMatcher(Map<String, String> when) {
        this.when = Objects.requireNonNull(Collections.unmodifiableMap(when));
        this.matcher = createMatcher();
    }

    //TODO:TJH - I have a feeling
    //some of the more complicated XML post matching stubs are going to be brittle
    //consider options for managing that - significant whitespace flag? (or more simply, xml/json)
    @SuppressWarnings("unchecked")
    private Matcher<? extends HttpUriRequest> createMatcher() {
        try {
            List<Matcher<HttpUriRequest>> result = new ArrayList<>(OPTIONAL_MATCHER_COUNT);
            if (when.containsKey(METHOD_KEY)) {
                result.add(hasMethod(equalTo(when.get(METHOD_KEY))));
            }

            String queryString = when.get(QUERY_KEY);
            if (queryString != null) {
                result.add(hasQueryString(equalTo(queryString)));
            }
            if (when.containsKey(REMOTE_ADDR_KEY)) {
                String remoteAddr = when.get(REMOTE_ADDR_KEY);
                result.add(hasHeader("X-Forwarded-For", hasItem(containsString(remoteAddr))));
            }
            if (when.containsKey(CONTENT_TYPE_KEY)) {
                result.add(hasContentType(containsString(when.get(CONTENT_TYPE_KEY))));
            }
            if (when.containsKey(CONTENT_KEY)) {
                result.add(hasContent(equalTo(when.get(CONTENT_KEY))));
            }
            if (when.containsKey(SCRIPT_KEY)) {
                result.add(scriptMatches(when.get(SCRIPT_KEY)));
            }
            return allOf(result.toArray(new Matcher[0]));
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    //<editor-fold desc="Object">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WhenMatcher)) return false;

        WhenMatcher that = (WhenMatcher) o;

        return when.equals(that.when);
    }

    @Override
    public int hashCode() { return when.hashCode(); }

    @Override
    public String toString() { return when.toString(); }
    //</editor-fold>

    //<editor-fold desc="Matcher">
    @Override
    public void describeTo(Description description) {
        description.appendValue(when);
    }

    @Override
    public boolean matches(Object item) {return matcher.matches(item);}

    @Override
    public void describeMismatch(Object item, Description mismatchDescription) {
        matcher.describeMismatch(item, mismatchDescription);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Deprecated
    public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
        matcher._dont_implement_Matcher___instead_extend_BaseMatcher_();
    }
    //</editor-fold>

    public Map<String, String> toMap() { return when; }
}
