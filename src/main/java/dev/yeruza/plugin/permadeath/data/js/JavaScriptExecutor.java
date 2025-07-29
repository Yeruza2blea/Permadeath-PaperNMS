package dev.yeruza.plugin.permadeath.data.js;

import dev.yeruza.plugin.permadeath.Permadeath;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JavaScriptExecutor {
    private static ScriptEngineManager manager;
    private static ScriptEngine engine;

    static {
        Thread.currentThread().setContextClassLoader(Permadeath.class.getClassLoader());

        manager = new ScriptEngineManager();
        engine = manager.getEngineByName("Nashorn");


        if (engine == null) {
            manager = new ScriptEngineManager(null);

            engine = manager.getEngineByName("Nashorn");
        }
    }

    public JavaScriptExecutor() {

    }



}
