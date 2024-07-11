package pl.genschu.bloomooemulator.interpreter;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private Map<String, Variable> variables = new HashMap<>();
	private String sceneName = "BRAKSCENY";
    private Object returnValue;

    public Variable getVariable(String name) {
        return variables.get(name);
    }

    public void setVariable(String name, Variable variable) {
        variables.put(name, variable);
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }

    public void removeVariable(String name) {
        variables.remove(name);
    }
	
	public void setSceneName(String sceneName)
	{
		this.sceneName = sceneName;
	}

	public String getSceneName()
	{
		return sceneName;
	}

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }
    public Object getReturnValue() {
        return returnValue;
    }
}

