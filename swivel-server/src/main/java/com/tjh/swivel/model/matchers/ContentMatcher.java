package com.tjh.swivel.model.matchers;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.io.IOException;

public class ContentMatcher extends FeatureMatcher<HttpUriRequest, String> {
    private static Logger logger = Logger.getLogger(ContentMatcher.class);

    public ContentMatcher(Matcher<? super String> subMatcher) {
        super(subMatcher, "HttpUriRequest content", "content");
    }

    @Override
    protected String featureValueOf(HttpUriRequest request) {
        String result = "";
        if ((request instanceof HttpEntityEnclosingRequest)) {
            try {
                result = EntityUtils.toString(((HttpEntityEnclosingRequest) request).getEntity());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    @Factory
    public static Matcher<HttpUriRequest> hasContent(Matcher<? super String> stringMatcher) {
        return new ContentMatcher(stringMatcher);
    }
}
