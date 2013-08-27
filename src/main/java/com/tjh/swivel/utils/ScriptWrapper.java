package com.tjh.swivel.utils;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * A very re-usable class. Perhaps it should be put into some utilities artifact?
 */
public class ScriptWrapper {

    protected final String script;
    protected ScriptEngine engine;
    protected CompiledScript compiledScript;

    public ScriptWrapper(String language, String script) throws ScriptException {
        this.script = script;
        this.engine = new ScriptEngineManager().getEngineByName(language);
        if (engine instanceof Compilable) {
            compiledScript = ((Compilable) engine).compile(script);
        }
    }

    public ScriptWrapper(String script) throws ScriptException { this("javascript", script); }

    public Object evalWith(Bindings bindings) throws ScriptException {
        Object result;
        if (compiledScript != null) {
            result = compiledScript.eval(bindings);
        } else {
            result = engine.eval(script, bindings);
        }
        return result;
    }
}
