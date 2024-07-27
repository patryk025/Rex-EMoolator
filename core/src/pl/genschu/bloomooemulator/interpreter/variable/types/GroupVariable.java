package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotFoundException;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class GroupVariable extends Variable {
	List<Variable> variables;

	public GroupVariable(String name, Context context) {
		super(name, context);

		this.variables = new ArrayList<>();

		this.setMethod("ADD", new Method(
			List.of(
				new Parameter("mixed", "varName1...varNameN", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				for(Object argument : arguments) {
					Variable variable = (Variable) argument;
					variables.add(variable);
				}
				return null;
			}
		});
		this.setMethod("ADDCLONES", new Method(
			List.of(
				new Parameter("STRING", "varName", true),
				new Parameter("INTEGER", "firstCloneIndex?", true),
				new Parameter("INTEGER", "numberOfClones", true)
			),
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ADDCLONES is not implemented yet");
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
		this.setMethod("NEXT", new Method(
			"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method NEXT is not implemented yet");
			}
		});
		this.setMethod("PREV", new Method(
			"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method PREV is not implemented yet");
			}
		});
		this.setMethod("REMOVE", new Method(
			List.of(
				new Parameter("STRING", "varName", true)
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
		this.setMethod("RESETMARKER", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method RESETMARKER is not implemented yet");
			}
		});
	}

	@Override
	public Method getMethod(String name, List<String> paramTypes) {
		try {
			return super.getMethod(name, paramTypes);
		} catch (ClassMethodNotFoundException e) {
			Gdx.app.log("GroupVariable", "Firing method " + name + " on every variable in group " + this.getName());
			return new Method("void") {

				@Override
				public Variable execute(List<Object> arguments) {
					for(Variable variable : variables) {
						try {
							variable.getMethod(name, paramTypes).execute(arguments);
						} catch (ClassMethodNotFoundException | ClassMethodNotImplementedException ignored) {
							// nothing to do
						}
					}

					return null;
				}
			};
		}
	}

	@Override
	public String getType() {
		return "GROUP";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		return; // no fields in this class
	}

}
