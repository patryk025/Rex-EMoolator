package pl.cba.genszu.amcodetranslator.interpreter.variable;

import java.util.List;

public abstract class Method {
    private List<Parameter> parameters;
    private String returnType;

    public Method(List<Parameter> parameters, String returnType) {
        this.parameters = parameters;
        this.returnType = returnType;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public abstract Object execute(List<Object> arguments);
}

