package com.tjh.swivel.model.matchers;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.io.IOException;

public class ContentMatcher extends FeatureMatcher<HttpUriRequest, String> {
    protected String consumedContent;

    public ContentMatcher(Matcher<? super String> subMatcher) {
        super(subMatcher, "HttpUriRequest content", "content");
    }

    @Override
    protected String featureValueOf(HttpUriRequest request) {
        //NOTE:TJH - content is consumed once, then no longer available from the reader, so we have to cache it
        if ((request instanceof HttpEntityEnclosingRequest) && consumedContent == null) {
            try {
                consumedContent = EntityUtils.toString(((HttpEntityEnclosingRequest) request).getEntity());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return consumedContent;
    }

    @Factory
    public static Matcher<HttpUriRequest> hasContent(Matcher<? super String> stringMatcher) {
        return new ContentMatcher(stringMatcher);
    }
}
