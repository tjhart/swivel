package swivel.model.matchers;

import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import javax.servlet.http.HttpServletRequest;

public class ContentTypeMatcher extends FeatureMatcher<HttpServletRequest, String> {
    public ContentTypeMatcher(Matcher<? super String> subMatcher) {
        super(subMatcher, "HttpServletRequest with contentType", "contentType");
    }

    @Override
    protected String featureValueOf(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getContentType();
    }

    @Factory
    public static Matcher<HttpServletRequest> hasContentType(Matcher<? super String> subMatcher) {
        return new ContentTypeMatcher(subMatcher);
    }
}
