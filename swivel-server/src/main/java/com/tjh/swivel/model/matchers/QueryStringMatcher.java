package com.tjh.swivel.model.matchers;

import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

public class QueryStringMatcher extends FeatureMatcher<HttpUriRequest, String> {
    public QueryStringMatcher(Matcher<? super String> subMatcher) {
        super(subMatcher, "HttpUriRequest with query", "query");
    }

    @Override
    protected String featureValueOf(HttpUriRequest actual) {
        return actual
                .getURI()
                .getQuery();
    }

    @Factory
    public static Matcher<HttpUriRequest> hasQueryString(Matcher<? super String> matcher) {
        return new QueryStringMatcher(matcher);
    }
}
