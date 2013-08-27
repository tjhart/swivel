package swivel.model;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class HttpServletRequestWrapperTest {

    private HttpServletRequest mockRequest;
    private HttpServletRequestWrapper requestWrapper;

    @Before
    public void setUp() throws Exception {
        mockRequest = mock(HttpServletRequest.class);
        requestWrapper = new HttpServletRequestWrapper(mockRequest);

        BufferedReader mockReader = mock(BufferedReader.class);

        when(mockRequest.getReader()).thenReturn(mockReader);
        when(mockReader.readLine()).thenReturn("content", null);
    }

    @Test
    public void getReaderRetrievesContent() throws IOException {
        HttpServletRequestWrapper wrapperSpy = spy(requestWrapper);
        doReturn("content")
                .when(wrapperSpy)
                .getContent();

        wrapperSpy.getReader();

        verify(wrapperSpy).getContent();
    }

    @Test
    public void getContentReturnsCachedContentIfNotNull() throws IOException {
        requestWrapper.content = "content";

        assertThat(requestWrapper.getContent(), equalTo(requestWrapper.content));
        verifyZeroInteractions(mockRequest);
    }

    @Test
    public void getContentDefersToDelegateReader() throws IOException {
        assertThat(requestWrapper.getContent(), equalTo("content"));
        verify(mockRequest).getReader();
    }

    @Test
    public void getInputStreamCreatesWrapperServletInputStream() throws IOException {
        assertThat(requestWrapper.getInputStream(), instanceOf(WrapperServletInputStream.class));
    }
}
