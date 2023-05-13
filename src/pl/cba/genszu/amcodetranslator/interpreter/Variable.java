package pl.cba.genszu.amcodetranslator.interpreter;

public class Variable<T> {
    private String name;
    private String type;
    private T value;

    public Variable(String name, String type, T value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    // Gettery i settery
}
