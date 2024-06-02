package pl.cba.genszu.amcodetranslator.interpreter.variable;

public class Parameter {
    private String type;
    private String name;
    private boolean mandatory;

    public Parameter(String type, String name, boolean mandatory) {
        this.type = type;
        this.name = name;
        this.mandatory = mandatory;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isMandatory() {
        return mandatory;
    }
}
