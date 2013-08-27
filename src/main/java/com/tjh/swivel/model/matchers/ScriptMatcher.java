package com.tjh.swivel.model.matchers;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import com.tjh.swivel.utils.ScriptWrapper;

import javax.script.Bindings;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.servlet.http.HttpServletRequest;

public class ScriptMatcher extends CustomMatcher<HttpServletRequest> {

    private final ScriptWrapper scriptWrapper;

    public ScriptMatcher(String language, String script) throws ScriptException {
        super(String.format("HttpServletRequest (request) evaluated by %1$s script:\n%2$s ", language, script));
        this.scriptWrapper = new ScriptWrapper(language, script);
    }

    public ScriptMatcher(String script) throws ScriptException { this("javascript", script); }

    @Override
    public boolean matches(Object o) {
        if (o == null || !(o instanceof HttpServletRequest)) {
            return false;
        }

        try {
            HttpServletRequest request = (HttpServletRequest) o;

            Bindings bindings = new SimpleBindings();
            bindings.put("request", request);
            return (Boolean) scriptWrapper.evalWith(bindings);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Factory
    public static Matcher<? super HttpServletRequest> scriptMatches(String script) throws ScriptException {
        return scriptMatches("javascript", script);
    }

    @Factory
    public static Matcher<? super HttpServletRequest> scriptMatches(String language, String script)
            throws ScriptException {
        return new ScriptMatcher(language, script);
    }
}
