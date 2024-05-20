package pl.cba.genszu.amcodetranslator.interpreter.ast;

import pl.cba.genszu.amcodetranslator.interpreter.Context;

public abstract class Statement extends Node {
    public abstract void execute(Context context);
}