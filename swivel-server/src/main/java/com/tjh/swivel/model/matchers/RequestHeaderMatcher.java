package com.tjh.swivel.model.matchers;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestHeaderMatcher extends FeatureMatcher<HttpUriRequest, Collection<String>> {
    private final String headerName;

    public RequestHeaderMatcher(String headerName, Matcher<? super Collection<String>> subMatcher) {
        super(subMatcher, String.format("HttpUriRequest with header named '%1$s'", headerName), headerName);
        this.headerName = headerName;
    }

    @Override
    protected Collection<String> featureValueOf(HttpUriRequest request) {
        return Stream.of(request.getHeaders(headerName))
                .map(Header::getValue)
                .collect(Collectors.toList());
    }

    @Factory
    public static Matcher<HttpUriRequest> hasHeader(String header,
            Matcher<? super Collection<String>> matcher) {
        return new RequestHeaderMatcher(header, matcher);
    }
}
