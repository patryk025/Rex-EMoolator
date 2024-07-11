package pl.genschu.bloomooemulator.interpreter.ast;

import pl.genschu.bloomooemulator.interpreter.Context;

public abstract class Statement extends Node {
    public abstract void execute(Context context);
}