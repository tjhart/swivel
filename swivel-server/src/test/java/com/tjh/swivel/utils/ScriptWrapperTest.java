package com.tjh.swivel.utils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScriptWrapperTest {

    private static final String SOURCE_SCRIPT = "(function(){})();";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private Bindings mockBindings;
    @Mock
    private ScriptWrapper scriptWrapper;
    @Mock
    private ScriptEngine mockScriptEngine;
    @Mock
    private CompiledScript mockCompiledScript;

    @Before
    public void before() throws ScriptException {
        scriptWrapper = new ScriptWrapper(SOURCE_SCRIPT);
    }
    @Test
    public void constructorCapturesScript() {
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

        scriptWrapper.compiledScript = mockCompiledScript;

        when(scriptWrapper.compiledScript.eval(any(Bindings.class)))
                .thenReturn("result");

        scriptWrapper.evalWith(mockBindings);

        verify(scriptWrapper.compiledScript).eval(mockBindings);
    }

    @Test
    public void evalWithDefersToEngineIfCompiledScriptNotAvailable() throws ScriptException {
        scriptWrapper.compiledScript = null;
        scriptWrapper.engine = mockScriptEngine;
        when(scriptWrapper.engine.eval(anyString(), any(Bindings.class))).thenReturn("result");

        scriptWrapper.evalWith(mockBindings);

        verify(scriptWrapper.engine).eval(SOURCE_SCRIPT, mockBindings);
    }
}
