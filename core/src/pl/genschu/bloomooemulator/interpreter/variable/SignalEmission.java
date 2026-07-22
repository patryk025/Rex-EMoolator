package pl.genschu.bloomooemulator.interpreter.variable;

import pl.genschu.bloomooemulator.interpreter.values.Value;

import java.util.List;

/**
 * One synchronous signal dispatch.
 *
 * @param emittedName full emitted name, including a qualifier when present
 * @param bindingName registered key selected by the dispatcher
 * @param arguments explicit payload, never the qualifier value
 * @param hasExplicitPayload whether payload replaces the binding's declared arguments
 */
public record SignalEmission(
    String emittedName,
    String bindingName,
    List<Value> arguments,
    boolean hasExplicitPayload
) {
    public SignalEmission {
        emittedName = emittedName != null ? emittedName : "";
        bindingName = bindingName != null ? bindingName : emittedName;
        arguments = arguments == null ? List.of() : List.copyOf(arguments);
    }

    public Value[] argumentArray() {
        return arguments.toArray(Value[]::new);
    }
}
