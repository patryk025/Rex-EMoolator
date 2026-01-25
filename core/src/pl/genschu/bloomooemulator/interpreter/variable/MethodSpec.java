package pl.genschu.bloomooemulator.interpreter.variable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Method spec that includes argument kinds.
 */
public record MethodSpec(
    VariableMethod method,
    List<ArgKind> argKinds
) {
    public MethodSpec {
        if (argKinds == null) {
            argKinds = List.of();
        }
    }

    public static MethodSpec of(VariableMethod method, ArgKind... kinds) {
        return new MethodSpec(method, kinds == null || kinds.length == 0 ? List.of() : List.of(kinds));
    }

    public ArgKind kindAt(int index) {
        if (index < 0 || index >= argKinds.size()) {
            return ArgKind.VALUE;
        }
        return argKinds.get(index);
    }

    public static Map<String, VariableMethod> toMethodMap(Map<String, MethodSpec> specs) {
        if (specs == null || specs.isEmpty()) {
            return Map.of();
        }
        Map<String, VariableMethod> map = new HashMap<>();
        for (Map.Entry<String, MethodSpec> entry : specs.entrySet()) {
            map.put(entry.getKey(), entry.getValue().method());
        }
        return Map.copyOf(map);
    }
}
