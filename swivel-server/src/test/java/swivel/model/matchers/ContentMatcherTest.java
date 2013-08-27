package swivel.model.matchers;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.tjh.swivel.model.matchers.ContentMatcher.hasContent;

public class ContentMatcherTest {

    @Test
    public void contentMatches() throws IOException {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        BufferedReader mockReader = mock(BufferedReader.class);

        when(mockRequest.getReader()).thenReturn(mockReader);
        String content = "My happy content";
        when(mockReader.readLine()).thenReturn(content, null);
        when(mockRequest.getContentLength()).thenReturn(content.length());

        assertThat(mockRequest, hasContent(equalTo(content)));
    }
}
