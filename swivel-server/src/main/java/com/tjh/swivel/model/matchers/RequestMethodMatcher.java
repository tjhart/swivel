package com.tjh.swivel.model.matchers;

import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import javax.servlet.http.HttpServletRequest;

public class RequestMethodMatcher extends FeatureMatcher<HttpServletRequest, String> {
    public RequestMethodMatcher(Matcher<? super String> subMatcher) {
        super(subMatcher, "HttpServletRequest with method", "method");
    }

    @Override
    protected String featureValueOf(HttpServletRequest request) { return request.getMethod(); }

    @Factory
    public static Matcher<HttpServletRequest> hasMethod(Matcher<? super String> subMatcher) {
        return new RequestMethodMatcher(subMatcher);
    }
}
