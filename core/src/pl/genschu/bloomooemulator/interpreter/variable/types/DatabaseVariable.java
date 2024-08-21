package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.DBLoader;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseVariable extends Variable {
	private StructVariable columns = null;
	private List<List<String>> data = new ArrayList<>();
	private int currentRow = 0;

	public DatabaseVariable(String name, Context context) {
		super(name, context);

		this.setMethod("FIND", new Method(
				List.of(
						new Parameter("STRING", "columnName", true),
						new Parameter("mixed", "columnValue", true),
						new Parameter("INTEGER", "defaultIndex?", true)
				),
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				String columnName = ArgumentsHelper.getString(arguments.get(0));
				String columnValue = ArgumentsHelper.getString(arguments.get(1));
				int defaultIndex = ArgumentsHelper.getInteger(arguments.get(2));

				if(columns == null) {
					Variable tmpColumns = context.getVariable(getAttribute("MODEL").getValue().toString());
					if(tmpColumns == null) {
						Gdx.app.error("DatabaseVariable", "Database doesn't have column information. Find is not possible");
						return new IntegerVariable("", defaultIndex, context);
					}
					else {
						columns = (StructVariable) tmpColumns;
						return new IntegerVariable("", find(columnName, columnValue, defaultIndex), context);
					}
				}
				else {
					return new IntegerVariable("", find(columnName, columnValue, defaultIndex), context);
				}
			}
		});
		this.setMethod("GETROWSNO", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				return new IntegerVariable("", getRowsNo(), context);
			}
		});
		this.setMethod("LOAD", new Method(
				List.of(
						new Parameter("STRING", "dtaName", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				String dtaName = ArgumentsHelper.getString(arguments.get(0));
				DBLoader.loadDatabase(DatabaseVariable.this, dtaName);
				return null;
			}
		});
		this.setMethod("NEXT", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				next();
				return null;
			}
		});
		this.setMethod("REMOVEALL", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				removeAll();
				return null;
			}
		});
		this.setMethod("SAVE", new Method(
				List.of(
						new Parameter("STRING", "dtaName", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				// TODO: implement this method
				throw new ClassMethodNotImplementedException("Method SAVE is not implemented yet");
			}
		});
		this.setMethod("SELECT", new Method(
				List.of(
						new Parameter("INTEGER", "rowIndex", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments, Variable variable) {
				Variable rowIndex = context.getVariable(ArgumentsHelper.getString(arguments.get(0)));
				if(rowIndex == null) {
					select(0);
				}
				else {
					try {
						select(Integer.parseInt(rowIndex.getValue().toString()));
					} catch (NumberFormatException e) {
						Gdx.app.error("DatabaseVariable", "Row index " + rowIndex.getValue() + " is not an integer. Selecting first row.");
						select(0);
					}
				}
				return null;
			}
		});
	}

	private int getRowsNo() {
		return data.size();
	}

	private int find(String columnName, String columnValue, int defaultIndex) {
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).get(columns.getFields().indexOf(columnName)).equals(columnValue)) {
				return i;
			}
		}
		return defaultIndex;
	}

	private void next() {
		currentRow++;
		if (currentRow >= data.size()) {
			currentRow = 0;
		}
	}

	private void removeAll() {
		data.clear();
		currentRow = 0;
	}

	private void select(int rowIndex) {
		if (rowIndex >= 0 && rowIndex < data.size()) {
			currentRow = rowIndex;
		}
	}

	@Override
	public String getType() {
		return "DATABASE";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("MODEL");
		if (knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

	public List<String> getCurrentRow() {
		return data.get(currentRow);
	}

	public StructVariable getColumns() {
		return columns;
	}

	public void setColumns(StructVariable columns) {
		this.columns = columns;
	}

	public List<List<String>> getData() {
		return data;
	}

	public void setData(List<List<String>> data) {
		this.data = data;
	}
}
