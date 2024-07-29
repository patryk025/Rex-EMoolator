package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;

public class StructVariable extends Variable {
	private List<String> fields;
	private List<String> types;
	private List<Variable> values;

	public StructVariable(String name, Context context) {
		super(name, context);

		this.setMethod("GETFIELD", new Method(
				List.of(
						new Parameter("INTEGER", "columnIndex", true)
				),
				"STRING"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				setAttributes();
				return getField((Integer) arguments.get(0));
			}
		});
		this.setMethod("SET", new Method(
				List.of(
						new Parameter("STRING", "varName", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				setAttributes();
				String varName = arguments.get(0).toString();
				set(varName);
				return null;
			}
		});
	}

	private Variable getField(int columnIndex) {
		if (columnIndex >= 0 && columnIndex < fields.size()) {
			return new StringVariable("", fields.get(columnIndex), context);
		}
		return null;
	}

	private void set(String varName) {
		DatabaseVariable db = (DatabaseVariable) getContext().getVariable(varName.split("_CURSOR")[0], null);
		if (db != null) {
			List<String> currentRow = db.getCurrentRow();
			for (int i = 0; i < fields.size(); i++) {
				setAttribute(fields.get(i), new Attribute("STRING", currentRow.get(i)));
			}
		}
	}

	@Override
	public String getType() {
		return "STRUCT";
	}

	public List<String> getFields() {
		setAttributes();
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public List<String> getTypes() {
		setAttributes();
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public List<Variable> getValues() {
		setAttributes();
		return values;
	}

	public void setValues(List<Variable> values) {
		this.values = values;
	}

	private void setAttributes() {
		if(fields != null) {
			return;
		}

		fields = new ArrayList<>();
		types = new ArrayList<>();
		values = new ArrayList<>();

		final String regex = "([A-Z]*)<([A-Z]*)>";

		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(getAttribute("FIELDS").getValue().toString());

		while (matcher.find()) {
			fields.add(matcher.group(1));
			types.add(matcher.group(2));
			values.add(VariableFactory.createVariable(types.get(types.size() - 1), null, null, context));
		}
	}
}
