package pl.genschu.bloomooemulator.interpreter.ast.statements;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.ast.Statement;
import pl.genschu.bloomooemulator.interpreter.exceptions.VariableNotFoundException;
import pl.genschu.bloomooemulator.interpreter.exceptions.VariableUnsupportedOperationException;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import static pl.genschu.bloomooemulator.interpreter.util.VariableHelper.getVariableFromObject;

public class ConvStatement extends Statement
{
	private Expression variableName;
	private String targetType;

	public ConvStatement(Expression variableName, String targetType)
	{
		this.variableName = variableName;
		this.targetType = targetType;
		
		if(this.targetType.startsWith("\"") && this.targetType.endsWith("\"")) {
			this.targetType = this.targetType.substring(1, this.targetType.length() - 1);
		}
	}
	
	@Override
	public void execute(Context context)
	{
		Variable var = getVariableFromObject(this.variableName, context);
		if(var == null) {
			throw new VariableNotFoundException(var.getName());
		}
		try {
			var = var.convertTo(targetType);
			context.setVariable(var.getName(), var);
			System.out.println("Converted "+var.getName()+" to "+var.getType());
			System.out.println("Debug: "+var.getValue());
		}
		catch(VariableUnsupportedOperationException e) {
			System.out.println("Błąd castowania zmiennych: "+e.getMessage());
		}
	}
}
