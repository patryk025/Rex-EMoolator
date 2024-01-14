package pl.cba.genszu.amcodetranslator.interpreter;

import java.util.*;
import pl.cba.genszu.amcodetranslator.interpreter.exceptions.*;
import pl.cba.genszu.amcodetranslator.interpreter.factories.*;
import pl.cba.genszu.amcodetranslator.interpreter.variabletypes.*;

public class Variable {
	private String name;

	private Map<String, String> signals;
	private List<Variable> clones;
	private Interpreter interpreter = null;

	public Variable(String name) {
		this.name = name;
		this.signals = new HashMap<>();
		this.clones = new ArrayList<>();
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

	public void addInterpreter(Interpreter interpreter) {
		this.interpreter = interpreter;
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
		switch (method) {
			case "ADDBEHAVIOUR":
				String signal = params[0].getValue().toString();
				String code = params[1].getValue().toString();
				this.signals.put(signal, code);
				return VariableFactory.createVariable("VOID", null);
			case "ADDTOARRAY":
				return VariableFactory.createVariable("VOID", null);
			case "ADDTOGROUP":
				return VariableFactory.createVariable("VOID", null);
			case "CLONE":
				int length = 0;
				if(params.length == 0) {
					length = 1;
				}
				else {
					length = (int) params[0].getValue();
				}
				for(int i = 0; i < length; i++) {
					try {
						this.clones.add((Variable) this.clone());
					}
					catch (CloneNotSupportedException e) {
						System.out.println("Błąd klonowania: " + e.getMessage());
						e.printStackTrace();
					}
				}
				return VariableFactory.createVariable("VOID", null);
			case "GETCLONEINDEX":
				return new IntegerVariable(null, this.clones.indexOf(this));
			case "GETCLONESNO":
				return new IntegerVariable(null, this.clones.size());
			case "GETNAME":
				return new StringVariable(null, this.getName());
			case "MSGBOX": // nie będzie raczej implementowane
				String message = params[0].getValue().toString();
				return VariableFactory.createVariable("VOID", null);
			case "OVERIDESIGNALS": // prawdopodobna implementacja
				signal = params[0].getValue().toString();
				code = params[1].getValue().toString();
				this.signals.replace(signal, code);
				return VariableFactory.createVariable("VOID", null);
			case "REMOVEALLBEHAVIOURS":
				this.signals.clear();
				return VariableFactory.createVariable("VOID", null);
			case "REMOVEBEHAVIOUR": // prawdopodobna implementacja
			case "REMOVEBEHAVIOURFORCE": // tego nie wiem, brak informacji o implementacji
				signal = params[0].getValue().toString();
				this.signals.remove(signal);
				return VariableFactory.createVariable("VOID", null);
			case "RESETCLONES":
				this.clones.clear(); // oryginalnie ustawiało zero klonów?
				return VariableFactory.createVariable("VOID", null);
			case "SEND":
				message = params[0].getValue().toString();
				code = this.signals.get(message);
				if(code == null) {
					throw new ClassBehaviorNotFoundException(message, this.getType());
				}
				interpreter.interpret(code);
				return VariableFactory.createVariable("VOID", null);
			default:
				throw new ClassMethodNotFoundException(method, this.getType());
		}
	}
	
	public void setProperty(String name, String value) throws ClassPropertyNotFoundException {
		throw new ClassPropertyNotFoundException(name, this.getType());
	}
}
