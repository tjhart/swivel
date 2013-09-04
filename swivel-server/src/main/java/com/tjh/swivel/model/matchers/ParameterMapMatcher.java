package com.tjh.swivel.model.matchers;

import com.tjh.swivel.utils.URIUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.List;
import java.util.Map;

public class ParameterMapMatcher extends FeatureMatcher<HttpUriRequest, Map<String, List<String>>> {

    public ParameterMapMatcher(Matcher<? super Map<String, List<String>>> subMatcher) {
        super(subMatcher, "HttpUriRequest with parameterMap", "parameterMap");
    }

    @Override
    protected Map<String, List<String>> featureValueOf(HttpUriRequest request) {
        return URIUtils.createMapFromQueryString(request.getURI().getQuery());
    }

    @Factory
    public static Matcher<HttpUriRequest> hasParameterMap(Matcher<Map<String, List<String>>> mapMatcher) {
        return new ParameterMapMatcher(mapMatcher);
    }
}
