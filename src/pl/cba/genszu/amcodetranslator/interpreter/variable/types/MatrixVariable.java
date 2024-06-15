package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class MatrixVariable extends Variable {
	public MatrixVariable(String name, Context context) {
		super(name, context);

		this.setMethod("CALCENEMYMOVEDEST", new Method(
			List.of(
				new Parameter("INTEGER", "oldCell", true),
				new Parameter("INTEGER", "directory?", true)
			),
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method CALCENEMYMOVEDEST is not implemented yet");
				return null;
			}
		});
		this.setMethod("CALCENEMYMOVEDIR", new Method(
			List.of(
				new Parameter("INTEGER", "oldCell", true),
				new Parameter("INTEGER", "oldDir?", true)
			),
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method CALCENEMYMOVEDIR is not implemented yet");
				return null;
			}
		});
		this.setMethod("CANHEROGOTO", new Method(
			List.of(
				new Parameter("INTEGER", "cellNo", true)
			),
			"BOOL"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method CANHEROGOTO is not implemented yet");
				return null;
			}
		});
		this.setMethod("GET", new Method(
			List.of(
				new Parameter("INTEGER", "cellNo", true)
			),
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GET is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETCELLOFFSET", new Method(
			List.of(
				new Parameter("INTEGER", "x", true),
				new Parameter("INTEGER", "y", true)
			),
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETCELLOFFSET is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETCELLPOSX", new Method(
			List.of(
				new Parameter("INTEGER", "cellNo", true)
			),
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETCELLPOSX is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETCELLPOSY", new Method(
			List.of(
				new Parameter("INTEGER", "cellNo", true)
			),
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETCELLPOSY is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETCELLSNO", new Method(
			List.of(
				new Parameter("INTEGER", "cellCode", true)
			),
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETCELLSNO is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETFIELDPOSX", new Method(
			List.of(
				new Parameter("INTEGER", "cellNo", true)
			),
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETFIELDPOSX is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETFIELDPOSY", new Method(
			List.of(
				new Parameter("INTEGER", "cellNo", true)
			),
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETFIELDPOSY is not implemented yet");
				return null;
			}
		});
		this.setMethod("GETOFFSET", new Method(
			List.of(
				new Parameter("INTEGER", "x", true),
				new Parameter("INTEGER", "y", true)
			),
			"INTEGER"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method GETOFFSET is not implemented yet");
				return null;
			}
		});
		this.setMethod("ISGATEEMPTY", new Method(
			"BOOL"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ISGATEEMPTY is not implemented yet");
				return null;
			}
		});
		this.setMethod("ISINGATE", new Method(
			List.of(
				new Parameter("INTEGER", "cellNo", true)
			),
			"BOOL"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method ISINGATE is not implemented yet");
				return null;
			}
		});
		this.setMethod("MOVE", new Method(
			List.of(
				new Parameter("INTEGER", "oldCell", true),
				new Parameter("INTEGER", "newCell", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method MOVE is not implemented yet");
				return null;
			}
		});
		this.setMethod("NEXT", new Method(
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method NEXT is not implemented yet");
				return null;
			}
		});
		this.setMethod("SET", new Method(
			List.of(
				new Parameter("INTEGER", "cellNo", true),
				new Parameter("INTEGER", "cellCode", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SET is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETGATE", new Method(
			List.of(
				new Parameter("INTEGER", "row", true),
				new Parameter("INTEGER", "col", true),
				new Parameter("INTEGER", "unknown", true),
				new Parameter("INTEGER", "unknown", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETGATE is not implemented yet");
				return null;
			}
		});
		this.setMethod("SETROW", new Method(
			List.of(
				new Parameter("INTEGER", "row", true),
				new Parameter("INTEGER", "cellCode1...cellCodeN", true)
			),
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method SETROW is not implemented yet");
				return null;
			}
		});
		this.setMethod("TICK", new Method(
			"void"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method TICK is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "MATRIX";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("INTEGER, INTEGER BASEPOS", "CELLHEIGHT", "CELLWIDTH", "INTEGER, INTEGER SIZE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
