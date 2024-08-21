package pl.genschu.bloomooemulator.interpreter.variable;

import java.util.ArrayList;
import java.util.List;

public abstract class Method {
    private List<Parameter> parameters;
    private List<String> parameterTypes;
    private String returnType;

    public Method(String returnType) {
        this(new ArrayList<>(), returnType);
    }

    public Method(List<Parameter> parameters, String returnType) {
        this.parameters = parameters;
        this.returnType = returnType;
        List<String> parameterTypes = new ArrayList<>();
        for (Parameter parameter : parameters) {
            String type = parameter.getType();
            if(!parameter.isMandatory()) {
                type = type + "?";
            }
            if(parameter.isVarArgs()) {
                parameterTypes.add(type + "...");
            } else {
                parameterTypes.add(type);
            }
        }
        this.parameterTypes = parameterTypes;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public List<String> getParameterTypes() {
        return new ArrayList<>(parameterTypes);
    }

    public String getReturnType() {
        return returnType;
    }

    public abstract Variable execute(List<Object> arguments, Variable var);
}
