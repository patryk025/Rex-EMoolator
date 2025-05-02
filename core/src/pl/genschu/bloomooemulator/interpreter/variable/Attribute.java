package pl.genschu.bloomooemulator.interpreter.variable;

public class Attribute {
    private String type;
    private Object value;

    public Attribute(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getInt() {
        return Integer.parseInt(value.toString());
    }

    public double getDouble() {
        return Double.parseDouble(value.toString());
    }

    public boolean getBool() {
        return value.toString().equals("TRUE");
    }

    public String getString() {
        return value.toString();
    }
}
