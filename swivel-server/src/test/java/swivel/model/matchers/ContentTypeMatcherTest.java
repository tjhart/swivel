package swivel.model.matchers;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.tjh.swivel.model.matchers.ContentTypeMatcher.hasContentType;

public class ContentTypeMatcherTest {

    @Test
    public void contentTypeMatches(){
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        when(mockRequest.getContentType()).thenReturn("application/xml");
        assertThat(mockRequest, hasContentType(equalTo("application/xml")));
    }
}
