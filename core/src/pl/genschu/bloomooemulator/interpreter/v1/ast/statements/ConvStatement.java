package pl.genschu.bloomooemulator.interpreter.v1.ast.statements;

import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.v1.ast.Statement;
import pl.genschu.bloomooemulator.interpreter.v1.exceptions.VariableNotFoundException;
import pl.genschu.bloomooemulator.interpreter.v1.exceptions.VariableUnsupportedOperationException;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Variable;

import static pl.genschu.bloomooemulator.interpreter.v1.util.VariableHelper.getVariableFromObject;

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
