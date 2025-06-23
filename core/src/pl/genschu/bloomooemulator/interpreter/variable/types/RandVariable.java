package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.variable.*;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandVariable extends GlobalVariable {
	public RandVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("GET", new Method(
				List.of(
						new Parameter("INTEGER", "offset", true),
						new Parameter("INTEGER", "range", false)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				int offset = 0;
				int range;
				if(arguments.size() >= 2) {
					offset = ArgumentsHelper.getInteger(arguments.get(0));
					range = ArgumentsHelper.getInteger(arguments.get(1));
				}
				else {
					range = ArgumentsHelper.getInteger(arguments.get(0));
				}

				return new IntegerVariable("", getRandom(offset, range), getContext());
			}
		});
		this.setMethod("GETPLENTY", new Method(
				List.of(
						new Parameter("STRING", "targetArray", true),
						new Parameter("INTEGER", "count", true),
						new Parameter("INTEGER", "offset", true),
						new Parameter("INTEGER", "range", true),
						new Parameter("BOOL", "onlyUnique", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				String targetArrayName = ArgumentsHelper.getString(arguments.get(0));
				int count = ArgumentsHelper.getInteger(arguments.get(1));
				int offset = ArgumentsHelper.getInteger(arguments.get(2));
				int range = ArgumentsHelper.getInteger(arguments.get(3));
				boolean onlyUnique = ArgumentsHelper.getBoolean(arguments.get(4));

				Variable targetArray = context.getVariable(targetArrayName);
				if(!(targetArray instanceof ArrayVariable)) {
					Gdx.app.error("RandVariable", "GETPLENTY method requires an ARRAY variable as target, got " + targetArray.getType() + " instead.");
                }
				else {
					List<Integer> elements = new ArrayList<>();

					if (onlyUnique && count > range) { // prevents getting stuck in an infinite loop (which happened in the original engine)
						Gdx.app.error("RandVariable", "Cannot generate " + count + " unique values in range [" + offset + ", " + (offset + range) + "] - too many unique values requested.");
						return null;
					}

					while (elements.size() < count) {
						int randomValue = getRandom(offset, range);
						if (onlyUnique && elements.contains(randomValue)) { // it only checks for uniqueness newly generated values, not existing ones
							continue; // Skip if the value is already in the list
						}
						elements.add(randomValue);
					}

					// Convert Integer list to Variable list
					List<Variable> variableElements = new ArrayList<>();
					for (Integer element : elements) {
						variableElements.add(new IntegerVariable("", element, getContext()));
					}

					// add all elements to the target array (not overwriting existing elements)
					((ArrayVariable) targetArray).getElements().addAll(variableElements);
                }
                return null;
            }
		});
	}

	private int getRandom(int range) {
		return getRandom(0, range);
	}

	private int getRandom(int offset, int range) {
		Random random = new Random();
		return random.nextInt(range) + offset;
	}

	@Override
	public String getType() {
		return "RAND";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		// no known parameters
    }

}
