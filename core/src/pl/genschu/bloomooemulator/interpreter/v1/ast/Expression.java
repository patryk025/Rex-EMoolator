package pl.genschu.bloomooemulator.interpreter.v1.ast;

import pl.genschu.bloomooemulator.interpreter.v1.Context;

public abstract class Expression extends Node {
    public abstract Object evaluate(Context context);
}
