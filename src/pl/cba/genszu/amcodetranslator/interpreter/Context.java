package pl.cba.genszu.amcodetranslator.interpreter;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private Map<String, Variable> variables = new HashMap<>();

    public Variable getVariable(String name) {
        return variables.get(name);
    }

    public void setVariable(String name, Variable variable) {
        variables.put(name, variable);
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }
}

