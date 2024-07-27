package pl.genschu.bloomooemulator.interpreter.variable;

public class Parameter {
    private String type;
    private String name;
    private boolean mandatory;
    private boolean varArgs;

    public Parameter(String type, String name, boolean mandatory) {
        this.type = type;
        this.name = name;
        this.mandatory = mandatory;
        this.varArgs = name.contains("...");
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

    public boolean isVarArgs() {
        return varArgs;
    }
}
