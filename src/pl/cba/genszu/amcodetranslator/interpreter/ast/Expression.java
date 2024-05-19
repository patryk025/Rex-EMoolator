package pl.cba.genszu.amcodetranslator.interpreter.ast;

import pl.cba.genszu.amcodetranslator.interpreter.Context;

public abstract class Expression extends Node {
    public abstract Object evaluate();

    public Object evaluate(Context context) {
        return evaluate();
    }
}
