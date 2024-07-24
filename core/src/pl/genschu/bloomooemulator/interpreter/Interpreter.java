package pl.genschu.bloomooemulator.interpreter;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.ast.Node;
import pl.genschu.bloomooemulator.interpreter.ast.Statement;
import pl.genschu.bloomooemulator.interpreter.exceptions.BreakException;

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
		try
		{
        	if (astRoot instanceof Statement)
			{
				((Statement) astRoot).execute(this.context);
				this.returnValue = null;
        	}
			else if (astRoot instanceof Expression)
			{
				this.returnValue = ((Expression) astRoot).evaluate(this.context);
				if(this.returnValue == null && this.context.getReturnValue() != null) {
					this.returnValue = this.context.getReturnValue();
				}
			}
		}
		catch (BreakException e)
		{
			Gdx.app.log("Interpreter", "BEHAVIOUR breaked");
		}
    }

    public Object getReturnValue()
	{
        return this.returnValue;
    }
}
