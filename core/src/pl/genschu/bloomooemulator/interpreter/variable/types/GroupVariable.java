package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotFoundException;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

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
			public Variable execute(List<Object> arguments, Variable variable) {
				for(Object argument : arguments) {
					Variable var = context.getVariable(ArgumentsHelper.getString(argument));
					variables.add(var);
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
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method ADDCLONES is not implemented yet");
			}
		});
		this.setMethod("GETSIZE", new Method(
			"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", variables.size(), context);
			}
		});
		this.setMethod("NEXT", new Method(
			"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method NEXT is not implemented yet");
			}
		});
		this.setMethod("PREV", new Method(
			"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
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
			public Variable execute(List<Object> arguments, Variable variable) {
				String varName = ArgumentsHelper.getString(arguments.get(0));

				for(Variable var : variables) {
					if(var.getName().equals(varName)) {
						variables.remove(var);
						return null;
					}
				}
				return null;
			}
		});
		this.setMethod("REMOVEALL", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				variables.clear();
				return null;
			}
		});
		this.setMethod("RESETMARKER", new Method(
			"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
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
				public Variable execute(List<Object> arguments, Variable variable) {
					for(Variable var : variables) {
						try {
							Gdx.app.log("GroupVariable", "Firing method " + name + " on variable " + variable.getName());
							var.getMethod(name, paramTypes).execute(arguments, var);
						} catch (ClassMethodNotFoundException | ClassMethodNotImplementedException ignored) {
							// nothing to do
							Gdx.app.log("GroupVariable", "Method " + name + " not found on variable " + variable.getName()+". Ignoring it.");
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
