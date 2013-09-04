package com.tjh.swivel.model.matchers;

import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

public class RequestedURIPathMatcher extends FeatureMatcher<HttpUriRequest, String> {
    public RequestedURIPathMatcher(Matcher<? super String> subMatcher) {
        super(subMatcher, "HttpUriRequest with URI Path", "uri path");
    }

    @Override
    protected String featureValueOf(HttpUriRequest request) {
        return request.getURI().getPath();
    }

    @Factory
    public static Matcher<HttpUriRequest> hasURIPath(Matcher<? super String> subMatcher) {
        return new RequestedURIPathMatcher(subMatcher);
    }
}
