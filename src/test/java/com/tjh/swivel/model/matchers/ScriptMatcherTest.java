package com.tjh.swivel.model.matchers;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;

public class ScriptMatcherTest {

    public static final String SIMPLE_SCRIPT = "(function(){" +
            "   return true;" +
            "})();";

    @Test
    public void scriptMatches() throws ScriptException {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        MatcherAssert.assertThat(mockRequest, ScriptMatcher.scriptMatches(SIMPLE_SCRIPT));
    }
}
