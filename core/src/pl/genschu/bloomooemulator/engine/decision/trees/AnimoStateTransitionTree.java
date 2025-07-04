package pl.genschu.bloomooemulator.engine.decision.trees;

import pl.genschu.bloomooemulator.engine.decision.DecisionTree;
import pl.genschu.bloomooemulator.engine.decision.builder.DecisionTreeBuilder;
import pl.genschu.bloomooemulator.engine.decision.events.AnimoEvent;
import pl.genschu.bloomooemulator.engine.decision.events.ButtonEvent;
import pl.genschu.bloomooemulator.engine.decision.states.AnimoState;
import pl.genschu.bloomooemulator.engine.decision.states.ButtonState;
import pl.genschu.bloomooemulator.interpreter.variable.types.AnimoVariable;
import pl.genschu.bloomooemulator.interpreter.variable.types.ButtonVariable;

public class AnimoStateTransitionTree {
    private static final DecisionTree<ButtonState> buttonStateTransitionTree = DecisionTreeBuilder.<ButtonState>create()
            .when(ctx -> getButtonEvent(ctx) == ButtonEvent.ENABLE)
                .then(ctx -> ButtonState.STANDARD)
            .andWhen(ctx -> getButtonEvent(ctx) == ButtonEvent.DISABLE)
                .then(ctx -> ButtonState.DISABLED)
            .andWhen(ctx -> getCurrentButtonState(ctx) == ButtonState.HOVERED && getButtonEvent(ctx) == ButtonEvent.PRESSED)
                .then(ctx -> ButtonState.PRESSED)
            .andWhen(ctx -> getCurrentButtonState(ctx) == ButtonState.PRESSED && getButtonEvent(ctx) == ButtonEvent.RELEASED)
                .then(ctx -> ButtonState.HOVERED)
            .andWhen(ctx -> getCurrentButtonState(ctx) == ButtonState.STANDARD && getButtonEvent(ctx) == ButtonEvent.FOCUS_ON)
                .then(ctx -> ButtonState.HOVERED)
            .andWhen(ctx -> getCurrentButtonState(ctx) == ButtonState.HOVERED && getButtonEvent(ctx) == ButtonEvent.FOCUS_OFF)
                .then(ctx -> ButtonState.STANDARD)
            .andWhen(ctx -> getCurrentButtonState(ctx) == ButtonState.PRESSED && getButtonEvent(ctx) == ButtonEvent.FOCUS_OFF)
                .then(ctx -> ButtonState.STANDARD)
            .otherwise(AnimoStateTransitionTree::getCurrentButtonState)
            .build();

    private static final DecisionTree<AnimoState> animoStateTransitionTree = DecisionTreeBuilder.<AnimoState>create()
            .when(ctx -> getAnimoEvent(ctx) == AnimoEvent.PLAY)
                .then(ctx -> AnimoState.PLAYING)
            .andWhen(ctx -> getAnimoEvent(ctx) == AnimoEvent.PAUSE && getCurrentAnimoState(ctx) == AnimoState.PLAYING)
                .then(ctx -> AnimoState.PAUSED)
            .andWhen(ctx -> getAnimoEvent(ctx) == AnimoEvent.STOP && (getCurrentAnimoState(ctx) == AnimoState.PLAYING || getCurrentAnimoState(ctx) == AnimoState.PAUSED))
                .then(ctx -> AnimoState.STOPPED)
            .andWhen(ctx -> getAnimoEvent(ctx) == AnimoEvent.END && getCurrentAnimoState(ctx) == AnimoState.PLAYING)
                .then(ctx -> AnimoState.IDLE)
            .otherwise(AnimoStateTransitionTree::getCurrentAnimoState)
            .build();

    private static ButtonEvent getButtonEvent(Object[] ctx) {
        return (ButtonEvent) ctx[1];
    }

    private static AnimoEvent getAnimoEvent(Object[] ctx) {
        return (AnimoEvent) ctx[1];
    }

    private static AnimoState getCurrentAnimoState(Object[] ctx) {
        return ((AnimoVariable) ctx[0]).getAnimationState();
    }

    private static ButtonState getCurrentButtonState(Object[] ctx) {
        return ((AnimoVariable) ctx[0]).getButtonState();
    }

    public static ButtonState evaluate(AnimoVariable variable, ButtonEvent event) {
        return buttonStateTransitionTree.evaluate(variable, event);
    }

    public static AnimoState evaluate(AnimoVariable variable, AnimoEvent event) {
        return animoStateTransitionTree.evaluate(variable, event);
    }
}
