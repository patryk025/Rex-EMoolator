package pl.cba.genszu.amcodetranslator.interpreter;

import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Node;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Statement;

public class Interpreter {
    private final Node astRoot;
    private final Context context;
    private Object returnValue;

    public Interpreter(Node astRoot, Context context) {
        this.astRoot = astRoot;
        this.context = context;
        this.returnValue = null;
    }

    public void interpret() {
        if (astRoot instanceof Statement) {
            ((Statement) astRoot).execute(this.context);
            this.returnValue = null;
        }
        else if (astRoot instanceof Expression) {
            this.returnValue = ((Expression) astRoot).evaluate(this.context);
        }
    }

    public Object getReturnValue() {
        return this.returnValue;
    }
}
