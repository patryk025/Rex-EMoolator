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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupVariable extends Variable {
	Set<Variable> variables;
	int marker = -1;

	public GroupVariable(String name, Context context) {
		super(name, context);

		this.variables = new HashSet<>();
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("ADD", new Method(
				List.of(
						new Parameter("mixed", "varName1...varNameN", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				for(Object argument : arguments) {
					Variable variable = context.getVariable(ArgumentsHelper.getString(argument));
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
				return new IntegerVariable("", variables.size(), context);
			}
		});
		this.setMethod("NEXT", new Method(
				"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				if(marker < variables.size()-1) {
					return variables.toArray(new Variable[0])[++marker];
				}
				return variables.toArray(new Variable[0])[marker];
			}
		});
		this.setMethod("PREV", new Method(
				"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				if(marker > 0) {
					return variables.toArray(new Variable[0])[--marker];
				}
				return variables.toArray(new Variable[0])[marker];
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
				String varName = ArgumentsHelper.getString(arguments.get(0));

				for(Variable variable : variables) {
					if(variable.getName().equals(varName)) {
						variables.remove(variable);
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
			public Variable execute(List<Object> arguments) {
				variables.clear();
				return null;
			}
		});
		this.setMethod("RESETMARKER", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				if(!variables.isEmpty()) {
					marker = 0;
				}
				else {
					marker = -1;
				}

				return null;
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
							Gdx.app.log("GroupVariable", "Firing method " + name + " on variable " + variable.getName());
							variable.getMethod(name, paramTypes).execute(arguments);
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
