package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("GETSIZE", new Method(
			List.of(),
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("GETSUMVALUE", new Method(
			List.of(),
			"INTEGER|DOUBLE"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("LOADINI", new Method(
			List.of(),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("REMOVEALL", new Method(
			List.of(),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				return null;
			}
		});
		this.setMethod("SAVEINI", new Method(
			List.of(),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
		List<String> knownAttributes = List.o);
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
