package com.tjh.swivel.utils;

import org.junit.Before;
import org.junit.Test;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScriptWrapperTest {

    private static final String SOURCE_SCRIPT = "(function(){})();";
    private ScriptWrapper scriptWrapper;
    private Bindings mockBindings;

    @Before
    public void before() throws ScriptException {
        scriptWrapper = new ScriptWrapper(SOURCE_SCRIPT);
        mockBindings = mock(Bindings.class);
    }
    @Test
    public void constructorCapturesScript() throws ScriptException {
        assertThat(scriptWrapper.script, sameInstance(SOURCE_SCRIPT));
    }

    @Test
    public void constructorCapturesScriptEngine() {
        assertThat(scriptWrapper.engine, notNullValue());
    }

    @Test
    public void constructorCompilesScript() {
        assertThat(scriptWrapper.compiledScript, notNullValue());
    }

    @Test
    public void evalWithInvokesCompiledScriptIfAvailable() throws ScriptException {
        scriptWrapper.compiledScript = mock(CompiledScript.class);

        when(scriptWrapper.compiledScript.eval(any(Bindings.class)))
                .thenReturn("result");

        scriptWrapper.evalWith(mockBindings);

        verify(scriptWrapper.compiledScript).eval(mockBindings);
    }

    @Test
    public void evalWithDefersToEngineIfCompiledScriptNotAvailable() throws ScriptException {
        scriptWrapper.compiledScript = null;
        scriptWrapper.engine = mock(ScriptEngine.class);
        when(scriptWrapper.engine.eval(anyString(), any(Bindings.class))).thenReturn("result");

        scriptWrapper.evalWith(mockBindings);

        verify(scriptWrapper.engine).eval(SOURCE_SCRIPT, mockBindings);
    }
}
