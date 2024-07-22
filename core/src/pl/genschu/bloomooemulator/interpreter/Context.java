package pl.genschu.bloomooemulator.interpreter;

import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private Map<String, Variable> variables = new HashMap<>();
    private Context parentContext;
    private Object returnValue;

    public Context() {
        this.parentContext = null;
    }

    public Context(Context parentContext) {
        this.parentContext = parentContext;
    }

    public Variable getVariable(String name) {
        Variable variable = variables.get(name);
        if (variable == null) {
            if(parentContext != null)
                return parentContext.getVariable(name);

            variable = VariableFactory.createVariable("STRING", name, null);

        }
        return variable;
    }

    public void setVariable(String name, Variable variable) {
        variables.put(name, variable);
    }

    public boolean hasVariable(String name) {
        if (variables.containsKey(name)) {
            return true;
        }
        return parentContext != null && parentContext.hasVariable(name);
    }

    public void removeVariable(String name) {
        variables.remove(name);
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public Context getParentContext() {
        return parentContext;
    }

    public void setParentContext(Context parentContext) {
        this.parentContext = parentContext;
    }
}