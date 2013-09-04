package com.tjh.swivel.model.matchers;

import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

public class RequestMethodMatcher extends FeatureMatcher<HttpUriRequest, String> {
    public RequestMethodMatcher(Matcher<? super String> subMatcher) {
        super(subMatcher, "HttpUriRequest with method", "method");
    }

    @Override
    protected String featureValueOf(HttpUriRequest request) { return request.getMethod(); }

    @Factory
    public static Matcher<HttpUriRequest> hasMethod(Matcher<? super String> subMatcher) {
        return new RequestMethodMatcher(subMatcher);
    }
}
