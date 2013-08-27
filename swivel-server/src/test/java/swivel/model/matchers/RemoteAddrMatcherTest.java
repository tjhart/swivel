package swivel.model.matchers;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.tjh.swivel.model.matchers.RemoteAddrMatcher.hasRemoteAddr;

public class RemoteAddrMatcherTest {

    @Test
    public void remoteAddressMatches(){
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        assertThat(mockRequest, hasRemoteAddr(equalTo("127.0.0.1")));
    }
}
