package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

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
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method CALCENEMYMOVEDEST is not implemented yet");
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
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method CALCENEMYMOVEDIR is not implemented yet");
			}
		});
		this.setMethod("CANHEROGOTO", new Method(
			List.of(
				new Parameter("INTEGER", "cellNo", true)
			),
			"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method CANHEROGOTO is not implemented yet");
			}
		});
		this.setMethod("GET", new Method(
			List.of(
				new Parameter("INTEGER", "cellNo", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GET is not implemented yet");
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
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETCELLOFFSET is not implemented yet");
			}
		});
		this.setMethod("GETCELLPOSX", new Method(
			List.of(
				new Parameter("INTEGER", "cellNo", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETCELLPOSX is not implemented yet");
			}
		});
		this.setMethod("GETCELLPOSY", new Method(
			List.of(
				new Parameter("INTEGER", "cellNo", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETCELLPOSY is not implemented yet");
			}
		});
		this.setMethod("GETCELLSNO", new Method(
			List.of(
				new Parameter("INTEGER", "cellCode", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETCELLSNO is not implemented yet");
			}
		});
		this.setMethod("GETFIELDPOSX", new Method(
			List.of(
				new Parameter("INTEGER", "cellNo", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETFIELDPOSX is not implemented yet");
			}
		});
		this.setMethod("GETFIELDPOSY", new Method(
			List.of(
				new Parameter("INTEGER", "cellNo", true)
			),
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETFIELDPOSY is not implemented yet");
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
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method GETOFFSET is not implemented yet");
			}
		});
		this.setMethod("ISGATEEMPTY", new Method(
			"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ISGATEEMPTY is not implemented yet");
			}
		});
		this.setMethod("ISINGATE", new Method(
			List.of(
				new Parameter("INTEGER", "cellNo", true)
			),
			"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ISINGATE is not implemented yet");
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
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method MOVE is not implemented yet");
			}
		});
		this.setMethod("NEXT", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method NEXT is not implemented yet");
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
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SET is not implemented yet");
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
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETGATE is not implemented yet");
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
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SETROW is not implemented yet");
			}
		});
		this.setMethod("TICK", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method TICK is not implemented yet");
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
