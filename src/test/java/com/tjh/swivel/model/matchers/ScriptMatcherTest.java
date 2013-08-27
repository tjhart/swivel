package com.tjh.swivel.model.matchers;

import com.tjh.swivel.utils.ScriptWrapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.script.Bindings;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

import static com.tjh.swivel.model.matchers.ScriptMatcher.scriptMatches;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScriptMatcherTest {

    public static final String SIMPLE_SCRIPT = "(function(){" +
            "   return true;" +
            "})();";
    private HttpServletRequest mockRequest;

    @Before
    public void setUp() throws Exception {
        mockRequest = mock(HttpServletRequest.class);
    }

    @Test
    public void scriptMatchesWorks() throws ScriptException {
        assertThat(mockRequest, scriptMatches(SIMPLE_SCRIPT));
    }

    @Test
    public void machesBindsRequestToEvaluation() throws ScriptException {
        ScriptMatcher scriptMatcher = new ScriptMatcher(SIMPLE_SCRIPT);

        scriptMatcher.scriptWrapper = mock(ScriptWrapper.class);
        when(scriptMatcher.scriptWrapper.evalWith(any(Bindings.class))).thenReturn(true);

        scriptMatcher.matches(mockRequest);

        ArgumentCaptor<Bindings> bindingsCaptor = ArgumentCaptor.forClass(Bindings.class);

        verify(scriptMatcher.scriptWrapper).evalWith(bindingsCaptor.capture());
        assertThat((HttpServletRequest)bindingsCaptor.getValue().get("request"), equalTo(mockRequest));
    }
}
