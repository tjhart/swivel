package com.tjh.swivel.model.matchers;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import vanderbilt.util.Block;
import vanderbilt.util.Lists;

import java.util.Collection;

public class RequestHeaderMatcher extends FeatureMatcher<HttpUriRequest, Collection<String>> {
    private final String headerName;

    public RequestHeaderMatcher(String headerName, Matcher<? super Collection<String>> subMatcher) {
        super(subMatcher, String.format("HttpUriRequest with header named '%1$s'", headerName), headerName);
        this.headerName = headerName;
    }

    @Override
    protected Collection<String> featureValueOf(HttpUriRequest request) {
        return Lists.collect(request.getHeaders(headerName), new Block<Header, String>() {
            @Override
            public String invoke(Header header) { return header.getValue(); }
        });
    }

    @Factory
    public static Matcher<HttpUriRequest> hasHeader(String header,
            Matcher<? super Collection<String>> matcher) {
        return new RequestHeaderMatcher(header, matcher);
    }
}
