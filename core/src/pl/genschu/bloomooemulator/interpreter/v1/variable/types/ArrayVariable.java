package pl.genschu.bloomooemulator.interpreter.v1.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.v1.arithmetic.operations.AddOperation;
import pl.genschu.bloomooemulator.interpreter.v1.arithmetic.operations.ModuloOperation;
import pl.genschu.bloomooemulator.interpreter.v1.arithmetic.operations.MultiplyOperation;
import pl.genschu.bloomooemulator.interpreter.v1.arithmetic.operations.SubtractOperation;
import pl.genschu.bloomooemulator.interpreter.v1.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.factories.LegacyVariableFactory;
import pl.genschu.bloomooemulator.interpreter.v1.logic.operations.EqualsOperation;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Method;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Variable;
import pl.genschu.bloomooemulator.loader.ArrayLoader;
import pl.genschu.bloomooemulator.saver.ArraySaver;
import pl.genschu.bloomooemulator.utils.LegacyArgumentsHelper;

import java.util.ArrayList;
import java.util.List;

public class ArrayVariable extends Variable implements Cloneable {
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
			public Variable execute(Variable self, List<Object> arguments) {
				for(Object argument : arguments) {
					((ArrayVariable) self).elements.add(((Variable) argument).clone());
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
			public Variable execute(Variable self, List<Object> arguments) {
				int index = LegacyArgumentsHelper.getInteger(arguments.get(0));

				if(index < 0 || index >= ((ArrayVariable) self).elements.size()) {
					Gdx.app.error("ArrayVariable", "Trying to set value in index out of bounds: "+index);
					return null;
				}

				AddOperation operation = new AddOperation();
				((ArrayVariable) self).elements.set(index, operation.performOperation(((ArrayVariable) self).elements.get(index), (Variable) arguments.get(1)));
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
			public Variable execute(Variable self, List<Object> arguments) {
				int index = LegacyArgumentsHelper.getInteger(arguments.get(0));

				if(index < 0 || index >= ((ArrayVariable) self).elements.size()) {
					Gdx.app.error("ArrayVariable", "Trying to set value in index out of bounds: "+index);
					return null;
				}

				((ArrayVariable) self).elements.set(index, ((Variable) arguments.get(1)).clone());
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
			public Variable execute(Variable self, List<Object> arguments) {
				int index = LegacyArgumentsHelper.getInteger(arguments.get(0));
				double rangeMin = LegacyArgumentsHelper.getDouble(arguments.get(1));
				double rangeMax = LegacyArgumentsHelper.getDouble(arguments.get(2));

				if(index < 0 || index >= ((ArrayVariable) self).elements.size()) {
					Gdx.app.error("ArrayVariable", "Trying to set value in index out of bounds: "+index);
					return null;
				}

				// use CLAMP method (all glory to the variable access by reference)
				((ArrayVariable) self).elements.get(index).fireMethod("CLAMP", new DoubleVariable("", rangeMin, self.getContext()), new DoubleVariable("", rangeMax, self.getContext()));
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
			public Variable execute(Variable self, List<Object> arguments) {
				String needle = LegacyArgumentsHelper.getString(arguments.get(0));

				for(Variable element : ((ArrayVariable) self).elements) {
					if(element.toString().equals(needle)) {
						return new BoolVariable("", true, self.getContext());
					}
				}

				return new BoolVariable("", false, self.getContext());
			}
		});
		this.setMethod("COPYTO", new Method(
				List.of(
						new Parameter("STRING", "arrayVarName", true)
				),
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				// TODO: check if it works this way
				Variable arrayVar = self.getContext().getVariable(LegacyArgumentsHelper.getString(arguments.get(0)));

				if(arrayVar.getType().equals("ARRAY")) {
					((ArrayVariable) arrayVar).elements.addAll(((ArrayVariable) self).elements);
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
			public Variable execute(Variable self, List<Object> arguments) {
				Variable needle = (Variable) arguments.get(0);

				EqualsOperation operation = new EqualsOperation();

				for(int i = 0; i < ((ArrayVariable) self).elements.size(); i++) {
					Variable element = ((ArrayVariable) self).elements.get(i);

					if(((BoolVariable) operation.performOperation(element, needle)).GET()) {
						return new IntegerVariable("", i, self.getContext());
					}
				}
				return new IntegerVariable("", -1, self.getContext());
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
			public Variable execute(Variable self, List<Object> arguments) {
				try {
					return ((ArrayVariable) self).elements.get(LegacyArgumentsHelper.getInteger(arguments.get(0)));
				} catch (IndexOutOfBoundsException e) {
					return new StringVariable("", "NULL", self.getContext());
				}
			}
		});
		this.setMethod("GETSIZE", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				return new IntegerVariable("", ((ArrayVariable) self).elements.size(), self.getContext());
			}
		});
		this.setMethod("GETSUMVALUE", new Method(
				"mixed"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				DoubleVariable sum = new DoubleVariable("", 0.0, self.getContext());
				AddOperation operation = new AddOperation();
				for(Variable element : ((ArrayVariable) self).elements) {
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
			public Variable execute(Variable self, List<Object> arguments) {
				int index = LegacyArgumentsHelper.getInteger(arguments.get(0));

				if(index < 0 || index >= ((ArrayVariable) self).elements.size()) {
					Gdx.app.error("ArrayVariable", "Trying to insert value in index out of bounds: "+index);
					return null;
				}

				((ArrayVariable) self).elements.add(index, ((Variable) arguments.get(1)).clone());
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
			public Variable execute(Variable self, List<Object> arguments) {
				((ArrayVariable) self).elements.clear();
				String path = LegacyArgumentsHelper.getString(arguments.get(0));
				ArrayLoader.loadArray(ArrayVariable.this, path);
				//debugArray();
				return null;
			}
		});
		this.setMethod("LOADINI", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				String serialized = self.getContext().getGame().getGameINI().get(self.getIniSection(), self.getName().toUpperCase());

				((ArrayVariable) self).elements.clear();
				if(serialized != null) {
                    if(serialized.isEmpty()) return null;
					String[] elems = serialized.split(",");
					for(String element : elems) {
						((ArrayVariable) self).elements.add(LegacyVariableFactory.createVariableWithAutoType("", element, self.getContext()));
					}
				}
				else {
                    Gdx.app.log("ARRAY", "No value in INI for " + self.getName());
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
			public Variable execute(Variable self, List<Object> arguments) {
				int index = LegacyArgumentsHelper.getInteger(arguments.get(0));

				if(index < 0 || index >= ((ArrayVariable) self).elements.size()) {
					Gdx.app.error("ArrayVariable", "Trying to set value in index out of bounds: "+index);
					return null;
				}

				ModuloOperation operation = new ModuloOperation();
				((ArrayVariable) self).elements.set(index, operation.performOperation(((ArrayVariable) self).elements.get(index), (Variable) arguments.get(1)));
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
			public Variable execute(Variable self, List<Object> arguments) {
				int index = LegacyArgumentsHelper.getInteger(arguments.get(0));

				if(index < 0 || index >= ((ArrayVariable) self).elements.size()) {
					Gdx.app.error("ArrayVariable", "Trying to set value in index out of bounds: "+index);
					return null;
				}

				MultiplyOperation operation = new MultiplyOperation();
				((ArrayVariable) self).elements.set(index, operation.performOperation(((ArrayVariable) self).elements.get(index), (Variable) arguments.get(1)));
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
			public Variable execute(Variable self, List<Object> arguments) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method REMOVE is not implemented yet");
			}
		});
		this.setMethod("REMOVEALL", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				((ArrayVariable) self).elements.clear();
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
			public Variable execute(Variable self, List<Object> arguments) {
				int index = LegacyArgumentsHelper.getInteger(arguments.get(0));

				if(index < 0 || index >= ((ArrayVariable) self).elements.size()) {
					Gdx.app.error("ArrayVariable", "Trying to remove value in index out of bounds: "+index);
					return null;
				}

				((ArrayVariable) self).elements.remove(index);
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
			public Variable execute(Variable self, List<Object> arguments) {
				Variable needle = (Variable) arguments.get(0);

				EqualsOperation operation = new EqualsOperation();

				for(int i = ((ArrayVariable) self).elements.size() - 1; i >= 0 ; i--) {
					Variable element = ((ArrayVariable) self).elements.get(i);

					if(((BoolVariable) operation.performOperation(element, needle)).GET()) {
						return new IntegerVariable("", i, self.getContext());
					}
				}
				return new IntegerVariable("", -1, self.getContext());
			}
		});
		this.setMethod("SAVE", new Method(
				List.of(
						new Parameter("STRING", "path", true)
				),
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				ArraySaver.saveArray(ArrayVariable.this, LegacyArgumentsHelper.getString(arguments.get(0)));
				return null;
			}
		});
		this.setMethod("SAVEINI", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				StringBuilder sb = new StringBuilder();

				for(int i = 0; i < ((ArrayVariable) self).elements.size(); i++) {
					Variable element = ((ArrayVariable) self).elements.get(i);
					sb.append(element.toString());

					if(i < ((ArrayVariable) self).elements.size() - 1) {
						sb.append(",");
					}
				}

				self.getContext().getGame().getGameINI().put(self.getIniSection(), self.getName().toUpperCase(), sb.toString());

				return null;
			}
		});
		this.setMethod("SUB", new Method(
				List.of(
						new Parameter("mixed", "value", true)
				),
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				SubtractOperation operation = new SubtractOperation();
				Variable valueToAdd = (Variable) arguments.get(0);
				((ArrayVariable) self).elements.replaceAll(var1 -> operation.performOperation(var1, valueToAdd));
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
			public Variable execute(Variable self, List<Object> arguments) {
				int index = LegacyArgumentsHelper.getInteger(arguments.get(0));

				if(index < 0 || index >= ((ArrayVariable) self).elements.size()) {
					Gdx.app.error("ArrayVariable", "Trying to set value in index out of bounds: "+index);
					return null;
				}

				SubtractOperation operation = new SubtractOperation();
				((ArrayVariable) self).elements.set(index, operation.performOperation(((ArrayVariable) self).elements.get(index), (Variable) arguments.get(1)));
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
			public Variable execute(Variable self, List<Object> arguments) {
				AddOperation operation = new AddOperation();
				Variable valueToAdd = (Variable) arguments.get(0);
				((ArrayVariable) self).elements.replaceAll(var1 -> operation.performOperation(var1, valueToAdd));
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

    @Override
    public ArrayVariable clone() {
        ArrayVariable clone = (ArrayVariable) super.clone();
		clone.setElements(new ArrayList<>(elements));
        return clone;
    }
}
