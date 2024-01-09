package pl.cba.genszu.amcodetranslator.interpreter;

import pl.cba.genszu.amcodetranslator.interpreter.exceptions.ClassMethodNotFoundException;
import pl.cba.genszu.amcodetranslator.interpreter.exceptions.VariableUnsupportedOperationException;
import pl.cba.genszu.amcodetranslator.interpreter.variabletypes.BoolVariable;
import pl.cba.genszu.amcodetranslator.interpreter.variabletypes.DoubleVariable;
import pl.cba.genszu.amcodetranslator.interpreter.variabletypes.IntegerVariable;
import pl.cba.genszu.amcodetranslator.interpreter.variabletypes.StringVariable;

public class Variable {
	private String name;

	public Variable(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		switch(this.getType()) {
			case "INTEGER":
				return ((IntegerVariable) this).GET();
			case "DOUBLE":
				return ((DoubleVariable) this).GET();
			case "BOOL":
				return ((BoolVariable) this).GET();
			case "STRING":
				return ((StringVariable) this).GET();
			default:
				return null; //nie ma potrzeby zwracać wartości z reszty obiektów
		}
	}

	public Variable convertTo(String type) {
		if(this.getType().equals(type)) // typ jest ten sam i nie ma po co konwertować
			return this;

		if(!(this.getType().equals("STRING") || this.getType().equals("BOOL") || this.getType().equals("INTEGER") || this.getType().equals("DOUBLE"))) {
			throw new VariableUnsupportedOperationException(this, "CONV");
		}

		if(!(type.equals("STRING") || type.equals("BOOL") || type.equals("INTEGER") || type.equals("DOUBLE"))) {
			throw new VariableUnsupportedOperationException(this, type, "CONV");
		}

		switch(this.getType()) {
			case "INTEGER":
				return ((IntegerVariable) this).convert(type);
			case "DOUBLE":
				return ((DoubleVariable) this).convert(type);
			case "BOOL":
				return ((BoolVariable) this).convert(type);
			case "STRING":
				return ((StringVariable) this).convert(type);
			default:
				return this;
		}
	}

	public String getType() {
		return this.getClass().getSimpleName().split("Variable")[0].toUpperCase();
	}

	public Variable fireFunction(String method, Variable... params) {
		throw new ClassMethodNotFoundException(method, this.getType());
	}
}
