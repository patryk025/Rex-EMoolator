package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class BehaviourVariable extends Variable {
	public BehaviourVariable(String name, Context context) {
		super(name, context);

		this.setMethod("PLAY", new Method(
			"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method PLAY is not implemented yet");
				return null;
			}
		});
		this.setMethod("RUN", new Method(
			List.of(
				new Parameter("mixed", "param1...paramN", true)
			),
			"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method RUN is not implemented yet");
				return null;
			}
		});
		this.setMethod("RUNC", new Method(
			List.of(
				new Parameter("mixed", "param1...paramN", true)
			),
			"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method RUNC is not implemented yet");
				return null;
			}
		});
		this.setMethod("RUNLOOPED", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "startVal", true),
				new Parameter("INTEGER|DOUBLE", "endDiff", true)
			),
			"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method RUNLOOPED is not implemented yet");
				return null;
			}
		});
		this.setMethod("RUNLOOPED", new Method(
			List.of(
				new Parameter("INTEGER|DOUBLE", "startVal", true),
				new Parameter("INTEGER|DOUBLE", "endDiff", true),
				new Parameter("INTEGER|DOUBLE", "incrementBy", true),
				new Parameter("mixed", "param1...paramN", true)
			),
			"mixed"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				// TODO: implement this method
				System.out.println("Method RUNLOOPED is not implemented yet");
				return null;
			}
		});
	}

	@Override
	public String getType() {
		return "BEHAVIOUR";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("CODE", "CONDITION");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
		}
	}

}
