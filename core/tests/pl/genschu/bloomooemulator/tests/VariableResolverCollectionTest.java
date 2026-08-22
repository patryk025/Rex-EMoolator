package pl.genschu.bloomooemulator.tests;

import org.junit.jupiter.api.Test;
import pl.genschu.bloomooemulator.interpreter.context.Context;
import pl.genschu.bloomooemulator.interpreter.runtime.ExecutionContext;
import pl.genschu.bloomooemulator.interpreter.variable.InstanceVariable;
import pl.genschu.bloomooemulator.interpreter.variable.TimerVariable;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class VariableResolverCollectionTest {

    @Test
    void nameKeyedCollectorPreservesTraversalOrderAndShadowing() {
        Context parent = context(null);
        Context scene = context(parent);
        Context instance = context(scene);
        Context additional = context(null);
        scene.addAdditionalContext(additional);
        scene.setVariable("WIDGET", new InstanceVariable("WIDGET", instance));

        TimerVariable classOnly = new TimerVariable("CLASS_ONLY", 0L);
        TimerVariable additionalOnly = new TimerVariable("ADDITIONAL_ONLY", 0L);
        TimerVariable localOnly = new TimerVariable("LOCAL_ONLY", 0L);
        TimerVariable parentOnly = new TimerVariable("PARENT_ONLY", 0L);
        TimerVariable classShared = new TimerVariable("SHARED", 0L);
        TimerVariable additionalShared = new TimerVariable("SHARED", 0L);
        TimerVariable localShared = new TimerVariable("SHARED", 0L);
        TimerVariable parentShared = new TimerVariable("SHARED", 0L);

        instance.setVariable("CLASS_ONLY", classOnly);
        instance.setVariable("SHARED", classShared);
        additional.setVariable("ADDITIONAL_ONLY", additionalOnly);
        additional.setVariable("SHARED", additionalShared);
        scene.setVariable("LOCAL_ONLY", localOnly);
        scene.setVariable("SHARED", localShared);
        parent.setVariable("PARENT_ONLY", parentOnly);
        parent.setVariable("SHARED", parentShared);

        Map<String, Variable> timers = scene.getTimerVariables();

        assertEquals(
            List.of("CLASS_ONLY", "SHARED", "ADDITIONAL_ONLY", "LOCAL_ONLY", "PARENT_ONLY"),
            new ArrayList<>(timers.keySet())
        );
        assertSame(classOnly, timers.get("CLASS_ONLY"));
        assertSame(additionalOnly, timers.get("ADDITIONAL_ONLY"));
        assertSame(localOnly, timers.get("LOCAL_ONLY"));
        assertSame(parentOnly, timers.get("PARENT_ONLY"));
        assertSame(parentShared, timers.get("SHARED"));
    }

    private static Context context(Context parent) {
        return new Context(new ExecutionContext(), parent);
    }
}
