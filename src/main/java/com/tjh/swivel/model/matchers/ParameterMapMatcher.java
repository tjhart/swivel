package com.tjh.swivel.model.matchers;

import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterMapMatcher extends FeatureMatcher<HttpServletRequest, Map<String, List<String>>> {

    public ParameterMapMatcher(Matcher<? super Map<String, List<String>>> subMatcher) {
        super(subMatcher, "HttpServletRequest with parameterMap", "parameterMap");
    }

    @Override
    protected Map<String, List<String>> featureValueOf(HttpServletRequest request) {
        Map<String, String[]> origMap = request.getParameterMap();
        Map<String, List<String>> resultMap = new HashMap<String, List<String>>(origMap.keySet().size());

        for (Map.Entry<String, String[]> stringEntry : origMap.entrySet()) {
            resultMap.put(stringEntry.getKey(), Arrays.asList(stringEntry.getValue()));
        }
        return resultMap;
    }

    @Factory
    public static Matcher<HttpServletRequest> hasParameterMap(Matcher<Map<String, List<String>>> mapMatcher) {
        return new ParameterMapMatcher(mapMatcher);
    }
}
