package pl.cba.genszu.amcodetranslator.interpreter.variable.types;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Attribute;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Method;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Parameter;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import java.util.List;

public class BehaviourVariable extends Variable {
	public BehaviourVariable(String name, Context context) {
		super(name, context);

		this.setMethod("PLAY", new Method(
			List.of(),
			"mixed"
		) {
			@Override
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
			public Object execute(List<Object> arguments) {
				// TODO: implement this method
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
