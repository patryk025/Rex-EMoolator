package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;

public class VectorVariable extends Variable {
	private double[] components;
	private int size;

	public VectorVariable(String name, Context context) {
		super(name, context);
		this.size = 0; // Default size
		this.components = new double[size];
	}

	@Override
	public String getType() {
		return "VECTOR";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("ADD", new Method(
				List.of(
						new Parameter("VECTOR", "otherVector", true)
				),
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				VectorVariable selfVar = (VectorVariable) self;
				if(arguments.size() != 1) {
					Gdx.app.error("VectorVariable", "Method ADD requires 1 argument, got " + arguments.size() + " instead.");
					return null;
				}
				VectorVariable otherVector;
				if(arguments.get(0) instanceof VectorVariable) {
					otherVector = (VectorVariable) arguments.get(0);
				}
				else if(arguments.get(0) instanceof StringVariable) {
					String argString = ArgumentsHelper.getString(arguments.get(0));
					Variable var = selfVar.getContext().getVariable(argString);
					if(!(var instanceof VectorVariable)) {
						Gdx.app.error("VectorVariable", "Method ADD requires VECTOR argument, got " + arguments.get(0).getClass().getSimpleName() + " instead.");
						return null;
					}
					else {
						otherVector = (VectorVariable) var;
					}
				}
				else {
					Gdx.app.error("VectorVariable", "Method ADD requires VECTOR argument, got " + arguments.get(0).getClass().getSimpleName() + " instead.");
					return null;
				}

				for (int i = 0; i < selfVar.size; i++) {
					selfVar.components[i] += otherVector.components[i];
				}
				return null;
			}
		});
		this.setMethod("ASSIGN", new Method(
				List.of(
						new Parameter("DOUBLE", "x...z", true)
				),
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				VectorVariable selfVar = (VectorVariable) self;
				if (arguments.size() > selfVar.components.length) {
					Gdx.app.log("VectorVariable", "Stretching vector from " + selfVar.components.length + " to " + arguments.size() + " components.");
					double[] newComponents = new double[arguments.size()];
					System.arraycopy(selfVar.components, 0, newComponents, 0, selfVar.components.length);
					for (int i = selfVar.components.length; i < newComponents.length; i++) {
						newComponents[i] = 0.0;
					}
					selfVar.components = newComponents;
				}

				for (int i = 0; i < arguments.size(); i++) {
					selfVar.components[i] = ArgumentsHelper.getDouble(arguments.get(i));
				}
				return null;
			}
		});
		this.setMethod("GET", new Method(
				List.of(
						new Parameter("INTEGER", "index", true)
				),
				"DOUBLE"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				VectorVariable selfVar = (VectorVariable) self;
				int index = ArgumentsHelper.getInteger(arguments.get(0));

				if (index >= 0 && index < selfVar.components.length) {
					return new DoubleVariable("", selfVar.components[index], selfVar.getContext());
				}
				return new DoubleVariable("", 0.0, selfVar.getContext());
			}
		});
		this.setMethod("MUL", new Method(
				List.of(
						new Parameter("DOUBLE", "scalar", true)
				),
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				VectorVariable selfVar = (VectorVariable) self;
				double scalar = ArgumentsHelper.getDouble(arguments.get(0));

				for (int i = 0; i < selfVar.size; i++) {
					selfVar.components[i] *= scalar;
				}
				return null;
			}
		});
		this.setMethod("NORMALIZE", new Method(
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				VectorVariable selfVar = (VectorVariable) self;
				double length = selfVar.length();

				if (length > 0) {
					for (int i = 0; i < selfVar.size; i++) {
						selfVar.components[i] /= length;
					}
				}
				return null;
			}
		});
		this.setMethod("LEN", new Method(
				"DOUBLE"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				VectorVariable selfVar = (VectorVariable) self;
				return new DoubleVariable("", selfVar.length(), selfVar.getContext());
			}
		});
		this.setMethod("REFLECT", new Method(
				List.of(
						new Parameter("VECTOR", "normalVector", true),
						new Parameter("VECTOR", "resultVector", true)
				),
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				VectorVariable selfVar = (VectorVariable) self;
				VectorVariable normalVector = (VectorVariable) arguments.get(0);
				VectorVariable resultVector = (VectorVariable) arguments.get(1);

				double dotProduct = 0;
				for (int i = 0; i < selfVar.size; i++) {
					dotProduct += selfVar.components[i] * normalVector.components[i];
				}

				for (int i = 0; i < selfVar.size; i++) {
					resultVector.components[i] = 2 * dotProduct * normalVector.components[i] - selfVar.components[i];
				}
				return null;
			}
		});
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("SIZE", "VALUE");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);

			switch (name) {
				case "SIZE":
					try {
						this.size = Integer.parseInt(attribute.getValue().toString());
						this.components = new double[size];
					} catch (NumberFormatException e) {
						this.size = 2;
						this.components = new double[2];
					}
					break;
				case "VALUE":
					String value = attribute.getValue().toString();
					String[] values = value.split(",");

					components = new double[values.length];

					for (int i = 0; i < values.length; i++) {
						try {
							components[i] = Double.parseDouble(values[i].trim());
						} catch (NumberFormatException e) {
							components[i] = 0.0;
						}
					}
			}
		}
	}

	private double length() {
		double sum = 0;
		for (double component : components) {
			sum += component * component;
		}
		return Math.sqrt(sum);
	}

	public double[] getComponents() {
		return components;
	}
}
