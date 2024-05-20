package pl.cba.genszu.amcodetranslator.interpreter;

import pl.cba.genszu.amcodetranslator.interpreter.ast.Node;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Statement;

public class Interpreter {
    private final Node astRoot;
    private final Context context;

    public Interpreter(Node astRoot, Context context) {
        this.astRoot = astRoot;
        this.context = context;
    }

    public void interpret() {
        if (astRoot instanceof Statement) {
            ((Statement) astRoot).execute(this.context);
        }
    }
}
