package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class ArrayVariable extends Variable {
	public ArrayVariable(String name, Context context) {
		super(name, context);

		this.setMethod("ADD", new Method(
			List.of(
				new Parameter("mixed", "param1...paramN", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ADD is not implemented yet");
				return null;
			}
		});
		this.setMethod("ADDAT", new Method(
			List.of(
				new Parameter("INTEGER", "index", true),
				new Parameter("mixed", "value", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ADDAT is not implemented yet");
				return null;
			}
		});
		this.setMethod("CHANGEAT", new Method(
			List.of(
				new Parameter("INTEGER", "index", true),
				new Parameter("mixed", "value", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method CHANGEAT is not implemented yet");
				return null;
			}
		});
		this.setMethod("CLAMPAT", new Method(
			List.of(
				new Parameter("INTEGER", "index", true),
				new Parameter("INTEGER|DOUBLE", "rangeMin", true),
				new Parameter("INTEGER|DOUBLE", "rangeMax", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method CLAMPAT is not implemented yet");
				return null;
			}
		});
		this.setMethod("CONTAINS", new Method(
			List.of(
				new Parameter("mixed", "needle", true)
			),
			"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method CONTAINS is not implemented yet");
				return null;
			}
		});
		this.setMethod("COPYTO", new Method(
			List.of(
				new Parameter("STRING", "arrayVarName", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method COPYTO is not implemented yet");
				return null;
			}
		});
		this.setMethod("FIND", new Method(
			List.of(
				new Parameter("mixed", "needle", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method FIND is not implemented yet");
				return null;
			}
		});
		this.setMethod("GET", new Method(
			List.of(
				new Parameter("INTEGER", "index", true)
			),
			"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GET is not implemented yet");
				return null;
			}
		});
		this.setMethod("GET", new Method(
			List.of(
				new Parameter("INTEGER", "index", true),
				new Parameter("mixed", "default", true)
			),
			"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GET is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETSIZE", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETSIZE is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETSUMVALUE", new Method(
			"INTEGER|DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETSUMVALUE is not implemented yet");
				return null;
			}
		});
		this.setMethod("INSERTAT", new Method(
			List.of(
				new Parameter("INTEGER", "index", true),
				new Parameter("mixed", "value", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method INSERTAT is not implemented yet");
				return null;
			}
		});
		this.setMethod("LOAD", new Method(
			List.of(
				new Parameter("INTEGER", "index", true),
				new Parameter("mixed", "value", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method LOAD is not implemented yet");
				return null;
			}
		});
		this.setMethod("LOADINI", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method LOADINI is not implemented yet");
				return null;
			}
		});
		this.setMethod("MODAT", new Method(
			List.of(
				new Parameter("INTEGER", "index", true),
				new Parameter("INTEGER|DOUBLE", "divisor", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method MODAT is not implemented yet");
				return null;
			}
		});
		this.setMethod("MULAT", new Method(
			List.of(
				new Parameter("INTEGER", "index", true),
				new Parameter("INTEGER|DOUBLE", "multiplier", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method MULAT is not implemented yet");
				return null;
			}
		});
		this.setMethod("REMOVE", new Method(
			List.of(
				new Parameter("mixed", "element", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method REMOVE is not implemented yet");
				return null;
			}
		});
		this.setMethod("REMOVEALL", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method REMOVEALL is not implemented yet");
				return null;
			}
		});
		this.setMethod("REMOVEAT", new Method(
			List.of(
				new Parameter("INTEGER", "index", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method REMOVEAT is not implemented yet");
				return null;
			}
		});
		this.setMethod("REVERSEFIND", new Method(
			List.of(
				new Parameter("mixed", "needle", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method REVERSEFIND is not implemented yet");
				return null;
			}
		});
		this.setMethod("SAVE", new Method(
			List.of(
				new Parameter("STRING", "path", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SAVE is not implemented yet");
				return null;
			}
		});
		this.setMethod("SAVEINI", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SAVEINI is not implemented yet");
				return null;
			}
		});
		this.setMethod("SUB", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "value", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SUB is not implemented yet");
				return null;
			}
		});
		this.setMethod("SUBAT", new Method(
			List.of(
				new Parameter("INTEGER", "index", true),
				new Parameter("INTEGER|DOUBLE", "value", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SUBAT is not implemented yet");
				return null;
			}
		});
		this.setMethod("SUM", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "value", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SUM is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "ARRAY";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		return; // no fields in this class
	}

}
