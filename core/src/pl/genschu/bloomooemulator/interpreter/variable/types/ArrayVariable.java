package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.arithmetic.operations.AddOperation;
import pl.genschu.bloomooemulator.interpreter.arithmetic.operations.ModuloOperation;
import pl.genschu.bloomooemulator.interpreter.arithmetic.operations.MultiplyOperation;
import pl.genschu.bloomooemulator.interpreter.arithmetic.operations.SubtractOperation;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.ArrayLoader;
import pl.genschu.bloomooemulator.saver.ArraySaver;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.ArrayList;
import java.util.List;

public class ArrayVariable extends Variable {
	List<Variable> elements;

	public ArrayVariable(String name, Context context) {
		super(name, context);

		this.elements = new ArrayList<>();
	}

	private void debugArray() {
		Gdx.app.log("ArrayVariable", "DEBUG "+getName()+" ("+elements.size()+" elements):");
		for(Variable element : elements) {
			Gdx.app.log("ArrayVariable <"+getName()+">", "\t"+element.getName()+" = "+element);
		}
	}

	@Override
	public String getType() {
		return "ARRAY";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("ADD", new Method(
				List.of(
						new Parameter("mixed", "param1...paramN", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				for(Object argument : arguments) {
					elements.add(((Variable) argument).clone());
				}
				//debugArray();
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
				int index = ArgumentsHelper.getInteger(arguments.get(0));
				AddOperation operation = new AddOperation();
				elements.set(index, operation.performOperation(elements.get(index), (Variable) arguments.get(1)));
				//debugArray();
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
				int index = ArgumentsHelper.getInteger(arguments.get(0));
				elements.set(index, ((Variable) arguments.get(1)).clone());
				//debugArray();
				return null;
			}
		});
		this.setMethod("CLAMPAT", new Method(
				List.of(
						new Parameter("INTEGER", "index", true),
						new Parameter("mixed", "rangeMin", true),
						new Parameter("mixed", "rangeMax", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int index = ArgumentsHelper.getInteger(arguments.get(0));
				double rangeMin = ArgumentsHelper.getDouble(arguments.get(1));
				double rangeMax = ArgumentsHelper.getDouble(arguments.get(2));

				// use CLAMP method (all glory to the variable access by reference)
				elements.get(index).fireMethod("CLAMP", new DoubleVariable("", rangeMin, context), new DoubleVariable("", rangeMax, context));
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
				String needle = ArgumentsHelper.getString(arguments.get(0));

				for(Variable element : elements) {
					if(element.toString().equals(needle)) {
						return new BoolVariable("", true, context);
					}
				}

				return new BoolVariable("", false, context);
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
				// TODO: check if it works this way
				Variable arrayVar = context.getVariable(ArgumentsHelper.getString(arguments.get(0)));

				if(arrayVar.getType().equals("ARRAY")) {
					((ArrayVariable) arrayVar).elements.addAll(elements);
				}

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
				Variable needle = (Variable) arguments.get(0);

				for(int i = 0; i < elements.size(); i++) {
					if(elements.get(i).toString().equals(needle.toString())) {
						return new IntegerVariable("", i, context);
					}
				}
				return new IntegerVariable("", -1, context);
			}
		});
		this.setMethod("GET", new Method(
				List.of(
						new Parameter("INTEGER", "index", true),
						new Parameter("mixed", "default", false)
				),
				"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				try {
					return elements.get(ArgumentsHelper.getInteger(arguments.get(0)));
				} catch (IndexOutOfBoundsException e) {
					if(arguments.size() > 1) {
						return (Variable) arguments.get(1);
					} else {
						return new IntegerVariable("", 0, context);
					}
				}
			}
		});
		this.setMethod("GETSIZE", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", elements.size(), context);
			}
		});
		this.setMethod("GETSUMVALUE", new Method(
				"INTEGER|DOUBLE"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				DoubleVariable sum = new DoubleVariable("", 0.0, context);
				AddOperation operation = new AddOperation();
				for(Variable element : elements) {
					sum = (DoubleVariable) operation.performOperation(sum, element);
				}
				return sum;
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
				int index = ArgumentsHelper.getInteger(arguments.get(0));
				elements.add(index, ((Variable) arguments.get(1)).clone());
				//debugArray();
				return null;
			}
		});
		this.setMethod("LOAD", new Method(
				List.of(
						new Parameter("mixed", "value", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				elements.clear();
				String path = ArgumentsHelper.getString(arguments.get(0));
				ArrayLoader.loadArray(ArrayVariable.this, path);
				//debugArray();
				return null;
			}
		});
		this.setMethod("LOADINI", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String serialized = context.getGame().getGameINI().get(context.getGame().getApplicationVariable().getName().toUpperCase(), getName().toUpperCase());

                elements.clear();
                if(serialized != null) {
                    if(serialized.isEmpty()) return null;
					String[] elems = serialized.split(",");
					for(String element : elems) {
						elements.add(VariableFactory.createVariableWithAutoType("", element, context));
					}
				}
				else {
                    Gdx.app.log("ARRAY", "No value in INI for " + getName());
				}

				return null;
			}
		});
		this.setMethod("MODAT", new Method(
				List.of(
						new Parameter("INTEGER", "index", true),
						new Parameter("mixed", "divisor", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int index = ArgumentsHelper.getInteger(arguments.get(0));
				ModuloOperation operation = new ModuloOperation();
				elements.set(index, operation.performOperation(elements.get(index), (Variable) arguments.get(1)));
				//debugArray();
				return null;
			}
		});
		this.setMethod("MULAT", new Method(
				List.of(
						new Parameter("INTEGER", "index", true),
						new Parameter("mixed", "multiplier", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int index = ArgumentsHelper.getInteger(arguments.get(0));
				MultiplyOperation operation = new MultiplyOperation();
				elements.set(index, operation.performOperation(elements.get(index), (Variable) arguments.get(1)));
				//debugArray();
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
				throw new ClassMethodNotImplementedException("Method REMOVE is not implemented yet");
			}
		});
		this.setMethod("REMOVEALL", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				elements.clear();
				//debugArray();
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
				int index = ArgumentsHelper.getInteger(arguments.get(0));
				try {
					elements.remove(index);
					//debugArray();
				} catch (IndexOutOfBoundsException e) {
					Gdx.app.log("ArrayVariable", "IndexOutOfBoundsException: "+index);
					// do nothing
				}
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
				Variable needle = (Variable) arguments.get(0);

				for(int i = elements.size() - 1; i >= 0 ; i--) {
					if(elements.get(i).toString().equals(needle.toString())) {
						return new IntegerVariable("", i, context);
					}
				}
				return new IntegerVariable("", -1, context);
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
				ArraySaver.saveArray(ArrayVariable.this, ArgumentsHelper.getString(arguments.get(0)));
				return null;
			}
		});
		this.setMethod("SAVEINI", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				StringBuilder sb = new StringBuilder();

				for(int i = 0; i < elements.size(); i++) {
					Variable element = elements.get(i);
					sb.append(element.toString());

					if(i < elements.size() - 1) {
						sb.append(",");
					}
				}

				context.getGame().getGameINI().put(context.getGame().getApplicationVariable().getName().toUpperCase(), getName().toUpperCase(), sb.toString());

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
				SubtractOperation operation = new SubtractOperation();
				Variable valueToAdd = (Variable) arguments.get(0);
				elements.replaceAll(var1 -> operation.performOperation(var1, valueToAdd));
				return null;
			}
		});
		this.setMethod("SUBAT", new Method(
				List.of(
						new Parameter("INTEGER", "index", true),
						new Parameter("mixed", "value", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int index = ArgumentsHelper.getInteger(arguments.get(0));
				SubtractOperation operation = new SubtractOperation();
				elements.set(index, operation.performOperation(elements.get(index), (Variable) arguments.get(1)));
				//debugArray();
				return null;
			}
		});
		this.setMethod("SUM", new Method(
				List.of(
						new Parameter("mixed", "value", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				AddOperation operation = new AddOperation();
				Variable valueToAdd = (Variable) arguments.get(0);
	            elements.replaceAll(var1 -> operation.performOperation(var1, valueToAdd));
				return null;
			}
		});
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		return; // no fields in this class
	}

	public List<Variable> getElements() {
		return elements;
	}

	public void setElements(List<Variable> elements) {
		this.elements = elements;
	}
}
