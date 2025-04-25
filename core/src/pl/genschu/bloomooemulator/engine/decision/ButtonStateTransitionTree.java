package pl.genschu.bloomooemulator.engine.decision;

import pl.genschu.bloomooemulator.engine.decision.builder.DecisionTreeBuilder;
import pl.genschu.bloomooemulator.engine.decision.events.ButtonEvent;
import pl.genschu.bloomooemulator.engine.decision.states.ButtonState;
import pl.genschu.bloomooemulator.interpreter.variable.types.ButtonVariable;

public class ButtonStateTransitionTree {
    private static final DecisionTree<ButtonState> stateTransitionTree = DecisionTreeBuilder.<ButtonState>create()
        .when(ctx -> getEvent(ctx) == ButtonEvent.ENABLE)
            .then(ctx -> ButtonState.STANDARD)
        .andWhen(ctx -> getEvent(ctx) == ButtonEvent.DISABLE)
            .then(ctx -> ButtonState.DISABLED)
        .andWhen(ctx -> getEvent(ctx) == ButtonEvent.DISABLE_BUT_VISIBLE)
            .then(ctx -> ButtonState.DISABLED_BUT_VISIBLE)
        .andWhen(ctx -> getCurrentState(ctx) == ButtonState.HOVERED && getEvent(ctx) == ButtonEvent.PRESSED)
            .then(ctx -> ButtonState.PRESSED)
        .andWhen(ctx -> getCurrentState(ctx) == ButtonState.PRESSED && getEvent(ctx) == ButtonEvent.RELEASED)
            .then(ctx -> ButtonState.HOVERED)
        .andWhen(ctx -> getCurrentState(ctx) == ButtonState.STANDARD && getEvent(ctx) == ButtonEvent.FOCUS_ON)
            .then(ctx -> ButtonState.HOVERED)
        .andWhen(ctx -> getCurrentState(ctx) == ButtonState.HOVERED && getEvent(ctx) == ButtonEvent.FOCUS_OFF)
            .then(ctx -> ButtonState.STANDARD)
        .andWhen(ctx -> getCurrentState(ctx) == ButtonState.PRESSED && getEvent(ctx) == ButtonEvent.FOCUS_OFF)
            .then(ctx -> ButtonState.STANDARD)
        .otherwise(ButtonStateTransitionTree::getCurrentState)
        .build();

    private static ButtonEvent getEvent(Object[] ctx) {
        return (ButtonEvent) ctx[1];
    }

    private static ButtonState getCurrentState(Object[] ctx) {
        return ((ButtonVariable) ctx[0]).getState();
    }

    public static ButtonState evaluate(ButtonVariable variable, ButtonEvent event) {
        return stateTransitionTree.evaluate(variable, event);
    }
}
