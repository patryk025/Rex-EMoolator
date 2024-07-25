package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
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
				throw new ClassMethodNotImplementedException("Method ADD is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method ADDAT is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method CHANGEAT is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method CLAMPAT is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method CONTAINS is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method COPYTO is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method FIND is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method GET is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method GET is not implemented yet");
			}
		});
		this.setMethod("GETSIZE", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETSIZE is not implemented yet");
			}
		});
		this.setMethod("GETSUMVALUE", new Method(
			"INTEGER|DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETSUMVALUE is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method INSERTAT is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method LOAD is not implemented yet");
			}
		});
		this.setMethod("LOADINI", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method LOADINI is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method MODAT is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method MULAT is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method REMOVE is not implemented yet");
			}
		});
		this.setMethod("REMOVEALL", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method REMOVEALL is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method REMOVEAT is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method REVERSEFIND is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method SAVE is not implemented yet");
			}
		});
		this.setMethod("SAVEINI", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SAVEINI is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method SUB is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method SUBAT is not implemented yet");
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
				throw new ClassMethodNotImplementedException("Method SUM is not implemented yet");
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
