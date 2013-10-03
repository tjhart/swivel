package com.tjh.swivel.model;

import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Map;

import static vanderbilt.util.Validators.notNull;

public class WhenMatcher implements Matcher<HttpUriRequest> {
    private final Matcher<? extends HttpUriRequest> matcher;
    private final Map<String, String> when;

    public WhenMatcher(Matcher<? extends HttpUriRequest> matcher, Map<String, String> when) {
        this.matcher = notNull("matcher", matcher);
        this.when = notNull("when", when);
    }

    //<editor-fold desc="Object">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WhenMatcher)) return false;

        WhenMatcher that = (WhenMatcher) o;

        return when.equals(that.when);
    }

    @Override
    public int hashCode() { return when.hashCode(); }

    @Override
    public String toString() { return when.toString(); }
    //</editor-fold>

    //<editor-fold desc="Matcher">
    @Override
    public void describeTo(Description description) {
        description.appendValue(when);
    }

    @Override
    public boolean matches(Object item) {return matcher.matches(item);}

    @Override
    public void describeMismatch(Object item, Description mismatchDescription) {
        matcher.describeMismatch(item, mismatchDescription);
    }

    @Override
    @Deprecated
    public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
        matcher._dont_implement_Matcher___instead_extend_BaseMatcher_();
    }
    //</editor-fold>


    public Map<String, String> getWhen() { return when; }
}
