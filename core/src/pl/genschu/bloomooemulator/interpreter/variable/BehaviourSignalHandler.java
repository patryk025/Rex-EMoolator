package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.values.StringValue;
import pl.genschu.bloomooemulator.interpreter.values.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** Debugger-friendly signal binding which executes a BEHAVIOUR synchronously. */
public final class BehaviourSignalHandler implements SignalHandler {
    private final String ownerName;
    private final String bindingName;
    private final BehaviourVariable behaviour;
    private final List<String> declaredArgumentTexts;
    private final MethodContext context;

    public BehaviourSignalHandler(String ownerName, String bindingName,
                                  BehaviourVariable behaviour, List<String> declaredArgumentTexts,
                                  MethodContext context) {
        this.ownerName = ownerName != null ? ownerName : "";
        this.bindingName = bindingName != null ? bindingName : "";
        this.behaviour = Objects.requireNonNull(behaviour, "behaviour");
        this.declaredArgumentTexts = declaredArgumentTexts == null
            ? List.of()
            : List.copyOf(declaredArgumentTexts);
        this.context = Objects.requireNonNull(context, "context");
    }

    public String ownerName() {
        return ownerName;
    }

    public String bindingName() {
        return bindingName;
    }

    public BehaviourVariable behaviour() {
        return behaviour;
    }

    public List<String> declaredArgumentTexts() {
        return declaredArgumentTexts;
    }

    @Override
    public void handle(Variable variable, String signalName, Value... arguments) {
        List<Value> payload = arguments == null ? List.of() : Arrays.asList(arguments);
        handle(variable, new SignalEmission(signalName, bindingName, payload, !payload.isEmpty()));
    }

    @Override
    public void handle(Variable variable, SignalEmission emission) {
        if (!BehaviourVariable.checkCondition(behaviour, context)) {
            Gdx.app.debug("BehaviourSignalHandler", "Signal " + emission.emittedName()
                + " on " + variable.name() + " skipped - CONDITION not met");
            return;
        }

        List<Value> effectiveArguments = emission.hasExplicitPayload()
            ? emission.arguments()
            : declaredArgumentTexts.stream().<Value>map(StringValue::new).toList();

        // Signals are top-level entry points. A BREAK has no caller to escape into.
        context.runBehaviour("Signal:" + emission.emittedName() + " on " + variable.name(),
            variable, behaviour, effectiveArguments);
    }

    @Override
    public String toString() {
        return "BehaviourSignalHandler[owner=" + ownerName + ", binding=" + bindingName
            + ", behaviour=" + behaviour.name() + ", declaredArgs=" + declaredArgumentTexts + "]";
    }
}
