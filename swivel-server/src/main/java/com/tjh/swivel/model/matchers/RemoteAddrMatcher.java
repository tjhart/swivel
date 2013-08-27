package com.tjh.swivel.model.matchers;

import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import javax.servlet.http.HttpServletRequest;

public class RemoteAddrMatcher extends FeatureMatcher<HttpServletRequest, String> {
    public RemoteAddrMatcher(Matcher<? super String> subMatcher) {
        super(subMatcher, "HttpServletRequest with remote address", "remote adress");
    }

    @Override
    protected String featureValueOf(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getRemoteAddr();
    }

    @Factory
    public static Matcher<HttpServletRequest> hasRemoteAddr(Matcher<? super String> subMatcher) {
        return new RemoteAddrMatcher(subMatcher);
    }
}
