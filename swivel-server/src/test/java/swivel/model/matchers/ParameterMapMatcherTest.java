package swivel.model.matchers;

import org.junit.Test;
import vanderbilt.util.Maps;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParameterMapMatcherTest {

    @Test
    public void queryParamsMatch() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        Map<String, String[]> parameterMap = Maps.asMap("key", new String[]{"value"});
        when(mockRequest.getParameterMap()).thenReturn(parameterMap);

        //NOTE:TJH - the copy is on purpose - we need to make sure that two different maps with the same
        //set of values are equal
        Map<String, List<String>> copy = Maps.asMap("key", Arrays.asList("value"));
        assertThat(mockRequest, ParameterMapMatcher.hasParameterMap(equalTo(copy)));
    }
}
