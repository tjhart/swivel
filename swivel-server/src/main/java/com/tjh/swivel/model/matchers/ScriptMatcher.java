package com.tjh.swivel.model.matchers;

import com.tjh.swivel.utils.ScriptWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import javax.script.Bindings;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

public class ScriptMatcher extends CustomMatcher<HttpUriRequest> {

    protected ScriptWrapper scriptWrapper;

    public ScriptMatcher(String language, String script) throws ScriptException {
        super(String.format("HttpUriRequest (request) evaluated by %1$s script:\n%2$s ", language, script));
        this.scriptWrapper = new ScriptWrapper(language, script);
    }

    public ScriptMatcher(String script) throws ScriptException { this("javascript", script); }

    @Override
    public boolean matches(Object o) {
        if (!(o instanceof HttpUriRequest)) {
            return false;
        }

        try {
            HttpUriRequest request = (HttpUriRequest) o;

            Bindings bindings = new SimpleBindings();
            bindings.put("request", request);
            return (Boolean) scriptWrapper.evalWith(bindings);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Factory
    public static Matcher<HttpUriRequest> scriptMatches(String script) throws ScriptException {
        return scriptMatches("javascript", script);
    }

    @Factory
    public static Matcher<HttpUriRequest> scriptMatches(String language, String script) throws ScriptException {
        return new ScriptMatcher(language, script);
    }
}
