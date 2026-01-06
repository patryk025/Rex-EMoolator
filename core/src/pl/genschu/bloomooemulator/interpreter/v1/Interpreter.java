package pl.genschu.bloomooemulator.interpreter.v1;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Node;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Statement;
import pl.genschu.bloomooemulator.interpreter.v1.exceptions.BreakException;
import pl.genschu.bloomooemulator.interpreter.v1.exceptions.OneBreakException;

@Deprecated(forRemoval = true, since = "0.2.0-beta")
public class Interpreter
{
    private final Node astRoot;
    private final Context context;
    private Object returnValue;

    public Interpreter(Node astRoot, Context context)
	{
        this.astRoot = astRoot;
        this.context = context;
        this.returnValue = null;
    }

    public void interpret()
	{
		try {
			if (astRoot instanceof Statement) {
				((Statement) astRoot).execute(this.context);
				this.returnValue = null;
			} else if (astRoot instanceof Expression) {
				this.returnValue = ((Expression) astRoot).evaluate(this.context);
				if (this.returnValue == null && this.context.getReturnValue() != null) {
					this.returnValue = this.context.getReturnValue();
				}
			}
		} catch(OneBreakException e) {
			Gdx.app.log("Interpreter", "OneBreak encountered. Breaking behaviour");
		} catch(BreakException e) {
			Gdx.app.log("Interpreter", "Break encountered. Breaking whole execution tree");
			throw e;
		}
    }

    public Object getReturnValue()
	{
        return this.returnValue;
    }
}
