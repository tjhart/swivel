package com.tjh.swivel.model.matchers;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

public class ContentTypeMatcher extends FeatureMatcher<HttpUriRequest, String> {
    public ContentTypeMatcher(Matcher<? super String> subMatcher) {
        super(subMatcher, "HttpUriRequest with contentType", "contentType");
    }

    @Override
    protected String featureValueOf(HttpUriRequest request) {
        if (!(request instanceof HttpEntityEnclosingRequest)) return null;
        return ((HttpEntityEnclosingRequest) request)
                .getEntity()
                .getContentType()
                .getValue();
    }

    @Factory
    public static Matcher<HttpUriRequest> hasContentType(Matcher<? super String> subMatcher) {
        return new ContentTypeMatcher(subMatcher);
    }
}
