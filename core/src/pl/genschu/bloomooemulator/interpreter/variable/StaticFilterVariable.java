package pl.genschu.bloomooemulator.interpreter.variable;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.annotations.InternalMutable;
import pl.genschu.bloomooemulator.engine.filters.Filter;
import pl.genschu.bloomooemulator.engine.filters.RotateFilter;
import pl.genschu.bloomooemulator.engine.filters.ScaleFilter;
import pl.genschu.bloomooemulator.interpreter.helpers.ArgumentHelper;
import pl.genschu.bloomooemulator.interpreter.values.*;

import java.util.*;

/**
 * StaticFilterVariable represents a graphical filter (rotate, scale, etc.)
 * that can be linked to Image/Animo variables.
 *
 * Uses mutable state for filter properties and linked objects.
 */
public record StaticFilterVariable(
    String name,
    String action,
    @InternalMutable FilterState state,
    Map<String, SignalHandler> signals
) implements Variable {

    public static final class FilterState {
        public final Map<String, Object> properties = new HashMap<>();
        public final Map<String, Filter> linkedFilters = new HashMap<>();

        public FilterState() {}

        public FilterState copy() {
            FilterState copy = new FilterState();
            copy.properties.putAll(this.properties);
            copy.linkedFilters.putAll(this.linkedFilters);
            return copy;
        }

        public void dispose() {
            linkedFilters.clear();
            properties.clear();
        }
    }

    public StaticFilterVariable {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        if (action == null) action = "";
        if (state == null) {
            state = new FilterState();
        }
        if (signals == null) {
            signals = Map.of();
        } else {
            signals = Map.copyOf(signals);
        }
    }

    public StaticFilterVariable(String name) {
        this(name, "", new FilterState(), Map.of());
    }

    public StaticFilterVariable(String name, String action) {
        this(name, action, new FilterState(), Map.of());
    }

    @Override
    public Value value() {
        return NullValue.INSTANCE;
    }

    @Override
    public VariableType type() {
        return VariableType.STATICFILTER;
    }

    @Override
    public Variable withValue(Value newValue) {
        return this;
    }

    @Override
    public Map<String, MethodSpec> methods() {
        return METHODS;
    }

    @Override
    public Variable withSignal(String signalName, SignalHandler handler) {
        Map<String, SignalHandler> newSignals = new HashMap<>(signals);
        if (handler != null) {
            newSignals.put(signalName, handler);
        } else {
            newSignals.remove(signalName);
        }
        return new StaticFilterVariable(name, action, state, newSignals);
    }

    @Override
    public Variable copyAs(String newName) {
        return new StaticFilterVariable(newName, action, state.copy(), new HashMap<>(signals));
    }

    private static Filter createFilterFromAction(String action) {
        if (action == null || action.isEmpty()) return null;
        return switch (action) {
            case "ROTATE" -> new RotateFilter();
            case "SCALE" -> new ScaleFilter();
            default -> {
                Gdx.app.error("StaticFilterVariable", "Unsupported filter action: " + action);
                yield null;
            }
        };
    }

    private static final Map<String, MethodSpec> METHODS = Map.ofEntries(
        Map.entry("SETPROPERTY", MethodSpec.of((self, args, ctx) -> {
            StaticFilterVariable filter = (StaticFilterVariable) self;
            String propertyName = ArgumentHelper.getString(args.get(0));
            Object propertyValue = args.get(1);
            if (propertyValue instanceof Value v) {
                // Unwrap Value to raw type for filter compatibility
                if (v instanceof IntValue iv) propertyValue = iv.value();
                else if (v instanceof DoubleValue dv) propertyValue = dv.value();
                else if (v instanceof StringValue sv) propertyValue = sv.value();
                else if (v instanceof BoolValue bv) propertyValue = bv.value();
            }
            filter.state.properties.put(propertyName, propertyValue);
            return MethodResult.noReturn();
        })),

        Map.entry("LINK", MethodSpec.of((self, args, ctx) -> {
            StaticFilterVariable filterVar = (StaticFilterVariable) self;
            String objectName = ArgumentHelper.getString(args.get(0));
            Variable object = ctx.getVariable(objectName);

            if (object == null) {
                Gdx.app.error("StaticFilterVariable", "Object not found: " + objectName);
                return MethodResult.noReturn();
            }

            Filter filter = createFilterFromAction(filterVar.action);
            if (filter == null) {
                Gdx.app.error("StaticFilterVariable", "Failed to create filter for action: " + filterVar.action);
                return MethodResult.noReturn();
            }

            for (Map.Entry<String, Object> entry : filterVar.state.properties.entrySet()) {
                filter.setProperty(entry.getKey(), entry.getValue());
            }

            if (object instanceof ImageVariable img) {
                img.addFilter(filter);
            } else if (object instanceof AnimoVariable animo) {
                animo.addFilter(filter);
            } else {
                Gdx.app.error("StaticFilterVariable", "Object type not supported: " + object.type());
                return MethodResult.noReturn();
            }

            filterVar.state.linkedFilters.put(objectName, filter);
            return MethodResult.noReturn();
        })),

        Map.entry("UNLINK", MethodSpec.of((self, args, ctx) -> {
            StaticFilterVariable filterVar = (StaticFilterVariable) self;
            String objectName = ArgumentHelper.getString(args.get(0));
            Variable object = ctx.getVariable(objectName);

            if (object == null) return MethodResult.noReturn();

            Filter filter = filterVar.state.linkedFilters.get(objectName);
            if (filter == null) return MethodResult.noReturn();

            if (object instanceof ImageVariable img) {
                img.removeFilter(filter);
            } else if (object instanceof AnimoVariable animo) {
                animo.removeFilter(filter);
            }

            filterVar.state.linkedFilters.remove(objectName);
            return MethodResult.noReturn();
        }))
    );

    @Override
    public String toString() {
        return "StaticFilterVariable[" + name + ", action=" + action + "]";
    }
}
