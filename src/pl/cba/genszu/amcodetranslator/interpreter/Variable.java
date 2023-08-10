package pl.cba.genszu.amcodetranslator.interpreter;

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

	public String getType() {
		return this.getClass().getSimpleName().split("Variable")[0].toUpperCase();
	}
}
