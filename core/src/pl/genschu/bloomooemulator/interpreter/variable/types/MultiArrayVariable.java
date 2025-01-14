package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.ArrayList;
import java.util.List;

public class MultiArrayVariable extends Variable {
	private List<Object> multiArray;
	private int dimensions;

	public MultiArrayVariable(String name, Context context) {
		super(name, context);
		this.multiArray = new ArrayList<>();
		this.dimensions = 1;
	}

	@Override
	public String getType() {
		return "MULTIARRAY";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("GET", new Method(
				List.of(
						new Parameter("INTEGER", "x", true),
						new Parameter("INTEGER", "y", false),
						new Parameter("INTEGER", "z...", false)
				),
				"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				List<Integer> indices = parseIndices(arguments);

				Object value = getValueAt(multiArray, indices, 0);

				if (value == null) {
					return new StringVariable("", "null", context);
				} else if (value instanceof Variable) {
					return (Variable) value;
				} else {
					return new StringVariable("", value.toString(), context);
				}
			}
		});
		this.setMethod("SET", new Method(
				List.of(
						new Parameter("INTEGER", "x", true),
						new Parameter("INTEGER", "y", false),
						new Parameter("INTEGER", "z...", false),
						new Parameter("mixed", "value", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				List<Integer> indices = parseIndices(arguments.subList(0, arguments.size() - 1));
				Object value = arguments.get(arguments.size() - 1);

				setValueAt(multiArray, indices, 0, value);

				return null;
			}
		});
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		if (name.equals("DIMENSIONS")) {
			this.dimensions = Integer.parseInt(attribute.getValue().toString());
			initializeArray();
		} else {
			super.setAttribute(name, attribute);
		}
	}

	private void initializeArray() {
		this.multiArray = createMultiArray(dimensions);
	}

	private List<Object> createMultiArray(int dimensions) {
		if (dimensions <= 1) {
			return new ArrayList<>();
		} else {
			List<Object> array = new ArrayList<>();
			array.add(createMultiArray(dimensions - 1));
			return array;
		}
	}

	private Object getValueAt(List<Object> array, List<Integer> indices, int depth) {
		if (depth == indices.size()) {
			return array;
		}

		int index = indices.get(depth);

		if (index >= array.size()) {
			return null;
		}

		Object value = array.get(index);

		if (value instanceof List<?>) {
			@SuppressWarnings("unchecked")
			List<Object> subList = (List<Object>) value;
			return getValueAt(subList, indices, depth + 1);
		} else {
			return value;
		}
	}

	private void setValueAt(List<Object> array, List<Integer> indices, int depth, Object value) {
		if (depth == indices.size() - 1) {
			if (indices.get(depth) >= array.size()) {
				for (int i = array.size(); i <= indices.get(depth); i++) {
					array.add(null);
				}
			}
			array.set(indices.get(depth), value);
			return;
		}

		int index = indices.get(depth);

		if (index >= array.size()) {
			for (int i = array.size(); i <= index; i++) {
				array.add(new ArrayList<>());
			}
		}

		@SuppressWarnings("unchecked")
		List<Object> subArray = (List<Object>) array.get(index);
		setValueAt(subArray, indices, depth + 1, value);
	}

	private List<Integer> parseIndices(List<Object> arguments) {
		List<Integer> indices = new ArrayList<>();
		for (Object arg : arguments) {
			indices.add(ArgumentsHelper.getInteger(arg));
		}
		return indices;
	}
}
