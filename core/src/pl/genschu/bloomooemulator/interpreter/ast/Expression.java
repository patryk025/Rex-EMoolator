package pl.genschu.bloomooemulator.interpreter.ast;

import pl.genschu.bloomooemulator.interpreter.Context;

public abstract class Expression extends Node {
    public abstract Object evaluate(Context context);
}
