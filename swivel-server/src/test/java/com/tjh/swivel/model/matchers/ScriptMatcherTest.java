package com.tjh.swivel.model.matchers;

import com.tjh.swivel.utils.ScriptWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.script.Bindings;
import javax.script.ScriptException;

import static com.tjh.swivel.model.matchers.ScriptMatcher.scriptMatches;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScriptMatcherTest {

    public static final String SIMPLE_SCRIPT = "(function(){" +
            "   return true;" +
            "})();";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private HttpUriRequest mockRequest;
    @Mock
    private ScriptWrapper mockScriptWrapper;

    @Test
    public void scriptMatchesWorks() throws ScriptException {
        assertThat(mockRequest, scriptMatches(SIMPLE_SCRIPT));
    }

    @Test
    public void machesBindsRequestToEvaluation() throws ScriptException {
        ScriptMatcher scriptMatcher = new ScriptMatcher(SIMPLE_SCRIPT);

        //REDTAG:TJH - fix this.
        scriptMatcher.scriptWrapper = mockScriptWrapper;
        when(scriptMatcher.scriptWrapper.evalWith(any(Bindings.class))).thenReturn(true);

        scriptMatcher.matches(mockRequest);

        ArgumentCaptor<Bindings> bindingsCaptor = ArgumentCaptor.forClass(Bindings.class);

        verify(scriptMatcher.scriptWrapper).evalWith(bindingsCaptor.capture());
        assertThat(bindingsCaptor.getValue().get("request"), equalTo(mockRequest));
    }
}
