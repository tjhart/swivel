package swivel.model.matchers;

import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

public class RequestedURIPathMatcher extends FeatureMatcher<HttpServletRequest, String> {
    public RequestedURIPathMatcher(Matcher<? super String> subMatcher) {
        super(subMatcher, "HttpServletRequest with URI Path", "uri path");
    }

    @Override
    protected String featureValueOf(HttpServletRequest request) {
        try {
            return new URI(request.getRequestURI()).getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Factory
    public static Matcher<HttpServletRequest> hasURIPath(Matcher<? super String> subMatcher) {
        return new RequestedURIPathMatcher(subMatcher);
    }
}
