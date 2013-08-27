package swivel.model.matchers;

import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class ContentMatcher extends FeatureMatcher<HttpServletRequest, String> {
    protected String consumedContent;

    public ContentMatcher(Matcher<? super String> subMatcher) {
        super(subMatcher, "HttpServletRequest with content", "content");
    }

    @Override
    protected String featureValueOf(HttpServletRequest request) {
        //NOTE:TJH - content is consumed once, then no longer available from the reader, so we have to cache it
        if (consumedContent == null) {
            try {
                int contentLength = request.getContentLength();
                StringBuilder stringBuilder = new StringBuilder(contentLength == -1 ? 5120 : contentLength);
                BufferedReader reader = request.getReader();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                consumedContent = stringBuilder.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return consumedContent;
    }

    @Factory
    public static Matcher<HttpServletRequest> hasContent(Matcher<? super String> stringMatcher) {
        return new ContentMatcher(stringMatcher);
    }
}
