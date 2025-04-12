package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.util.List;

public class TimerVariable extends Variable {
	private long elapse;
	private boolean enabled;
	private int ticks;
	private long lastTickTime;
	private int currentTickCount;

	public TimerVariable(String name, Context context) {
		super(name, context);

		this.enabled = true;
	}

	@Override
	public String getType() {
		return "TIMER";
	}

	@Override
	protected void setMethods() {
		super.setMethods();

		this.setMethod("DISABLE", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				enabled = false;
				return null;
			}
		});
		this.setMethod("ENABLE", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				enabled = true;
				lastTickTime = System.currentTimeMillis();
				return null;
			}
		});
		this.setMethod("GETTICKS", new Method(
				"INTEGER"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				return new IntegerVariable("", currentTickCount, getContext());
			}
		});
		this.setMethod("RESET", new Method(
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				currentTickCount = 0;
				lastTickTime = System.currentTimeMillis();
				return null;
			}
		});
		this.setMethod("SET", new Method(
				List.of(
						new Parameter("INTEGER", "timeMs", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				ticks = ArgumentsHelper.getInteger(arguments.get(0));
				return null;
			}
		});
		this.setMethod("SETELAPSE", new Method(
				List.of(
						new Parameter("INTEGER", "timeMs", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				elapse = ArgumentsHelper.getInteger(arguments.get(0));
				// reset timer
				lastTickTime = System.currentTimeMillis();
				return null;
			}
		});
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		switch (name) {
			case "ELAPSE":
				elapse = Long.parseLong(attribute.getValue().toString());
				if (enabled) {
					lastTickTime = System.currentTimeMillis();
				}
				break;
			case "ENABLED":
				enabled = attribute.getValue().toString().equals("TRUE");
				break;
			case "TICKS":
				ticks = Integer.parseInt(attribute.getValue().toString());
				break;
			default:
				super.setAttribute(name, attribute);
				break;
		}
	}

	public void update() {
		if (enabled) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastTickTime >= elapse) {
				lastTickTime = currentTime;
				currentTickCount++;
				emitSignal("ONTICK", currentTickCount);
				if (ticks != 0 && currentTickCount >= ticks) {
					enabled = false;
				}
			}
		}
	}

	public long getElapse() {
		return elapse;
	}

	public int getTicks() {
		return ticks;
	}

	public int getCurrentTickCount() {
		return currentTickCount;
	}

	public int getTimeFromLastTick() {
		return (int) (System.currentTimeMillis() - lastTickTime);
	}
}
