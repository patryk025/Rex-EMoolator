package pl.genschu.bloomooemulator.interpreter.v1.ast;

import pl.genschu.bloomooemulator.interpreter.v1.Context;

public abstract class Statement extends Node {
    public abstract void execute(Context context);
}