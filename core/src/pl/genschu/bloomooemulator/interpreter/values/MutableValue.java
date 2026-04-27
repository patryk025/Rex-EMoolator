package pl.genschu.bloomooemulator.interpreter.values;

/**
 * Mutable wrapper around an immutable {@link Value}.
 * Used inside record-based Variables so that method calls can
 * mutate the value in-place instead of creating new record instances.
 */
public final class MutableValue {
    private Value value;

    public MutableValue(Value value) {
        this.value = value;
    }

    public Value get() {
        return value;
    }

    public void set(Value value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MutableValue other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "MutableValue[" + value + "]";
    }
}
