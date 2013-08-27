package com.tjh.swivel.model.matchers;

import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import vanderbilt.util.Block;
import vanderbilt.util.Lists;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class HeaderMatcher extends FeatureMatcher<HttpServletRequest, List<String>> {
    private final String headerName;

    public HeaderMatcher(String headerName, Matcher<? super List<String>> subMatcher) {
        super(subMatcher, String.format("HttpServletRequest with header named '%1$s'", headerName), headerName);
        this.headerName = headerName;
    }

    @Override
    protected List<String> featureValueOf(HttpServletRequest request) {
        ArrayList<String> result = null;
        String header = request.getHeader(headerName);
        if (header != null) {
            String[] elements = header.split(",");
            result = Lists.collect(elements, new ArrayList<String>(elements.length), new Block<String, String>() {
                @Override
                public String invoke(String s) { return s.trim(); }
            });
        }
        return result;
    }

    @Factory
    public static Matcher<? super HttpServletRequest> hasHeader(String header, Matcher<? super List<String>> matcher) {
        return new HeaderMatcher(header, matcher);
    }
}
