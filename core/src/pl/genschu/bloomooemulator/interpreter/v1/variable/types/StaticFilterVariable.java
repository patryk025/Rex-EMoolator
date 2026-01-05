package pl.genschu.bloomooemulator.interpreter.v1.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.engine.filters.Filter;
import pl.genschu.bloomooemulator.engine.filters.RotateFilter;
import pl.genschu.bloomooemulator.engine.filters.ScaleFilter;
import pl.genschu.bloomooemulator.interpreter.v1.Context;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Method;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.v1.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticFilterVariable extends Variable {
	private String action;
	private final Map<String, Object> properties = new HashMap<>();
	private final Map<Variable, Filter> linkedFilters = new HashMap<>();

	public StaticFilterVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	public String getType() {
		return "STATICFILTER";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("SETPROPERTY", new Method(
				List.of(
						new Parameter("STRING", "filterName", true),
						new Parameter("mixed", "value", true)
				),
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				StaticFilterVariable selfVar = (StaticFilterVariable) self;
				String propertyName = ArgumentsHelper.getString(arguments.get(0));
				Object propertyValue = arguments.get(1);

				selfVar.properties.put(propertyName, propertyValue);

				return null;
			}
		});

		this.setMethod("LINK", new Method(
				List.of(
						new Parameter("STRING", "imageName", true)
				),
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				StaticFilterVariable selfVar = (StaticFilterVariable) self;
				String objectName = ArgumentsHelper.getString(arguments.get(0));
				Variable object = selfVar.getContext().getVariable(objectName);

				if (object == null) {
					Gdx.app.error("StaticFilterVariable", "Object not found: " + objectName);
					return null;
				}

				if (!(object instanceof ImageVariable) && !(object instanceof AnimoVariable)) {
					Gdx.app.error("StaticFilterVariable", "Object type not supported: " + object.getType());
					return null;
				}

				Filter filter = selfVar.createFilterFromAction(selfVar.action);
				if (filter == null) {
					Gdx.app.error("StaticFilterVariable", "Failed to create filter for action: " + selfVar.action);
					return null;
				}

				for (Map.Entry<String, Object> entry : selfVar.properties.entrySet()) {
					filter.setProperty(entry.getKey(), entry.getValue());
				}

				if (object instanceof ImageVariable) {
					((ImageVariable) object).addFilter(filter);
				} else {
					((AnimoVariable) object).addFilter(filter);
				}

				selfVar.linkedFilters.put(object, filter);

				return null;
			}
		});

		this.setMethod("UNLINK", new Method(
				List.of(
						new Parameter("STRING", "imageName", true)
				),
				"void"
		) {
			@Override
			public Variable execute(Variable self, List<Object> arguments) {
				StaticFilterVariable selfVar = (StaticFilterVariable) self;
				String objectName = ArgumentsHelper.getString(arguments.get(0));
				Variable object = selfVar.getContext().getVariable(objectName);

				if (object == null) {
					return null;
				}

				Filter filter = selfVar.linkedFilters.get(object);
				if (filter == null) {
					return null;
				}

				if (object instanceof ImageVariable) {
					((ImageVariable) object).removeFilter(filter);
				} else if (object instanceof AnimoVariable) {
					((AnimoVariable) object).removeFilter(filter);
				}

				selfVar.linkedFilters.remove(object);

				return null;
			}
		});
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		if (name.equals("ACTION")) {
			action = attribute.getValue().toString();
		} else {
			super.setAttribute(name, attribute);
		}
	}

	private Filter createFilterFromAction(String action) {
		if (action == null) {
			return null;
		}

		switch (action) {
			case "ROTATE":
				return new RotateFilter();
			case "SCALE":
				return new ScaleFilter();
			default:
				Gdx.app.error("StaticFilterVariable", "Unsupported filter action: " + action);
				return null;
		}
	}
}