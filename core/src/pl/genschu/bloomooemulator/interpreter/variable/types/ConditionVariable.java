package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Interpreter;
import pl.genschu.bloomooemulator.interpreter.ast.statements.BreakStatement;
import pl.genschu.bloomooemulator.interpreter.exceptions.BreakException;
import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.exceptions.OneBreakException;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.loader.CNVParser;
import pl.genschu.bloomooemulator.utils.ArgumentsHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ConditionVariable extends Variable {
	private BehaviourVariable behaviourVariable;

	public ConditionVariable(String name, Context context) {
		super(name, context);

		this.setMethod("BREAK", new Method(
				List.of(
						new Parameter("BOOL", "expectedResult", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				boolean result = check();
				boolean expectedResult = ArgumentsHelper.getBoolean(arguments.get(0));
				if(result == expectedResult) {
					throw new BreakException("Break statement encountered");
				}
				return null;
			}
		});
		this.setMethod("CHECK", new Method(
				List.of(
						new Parameter("BOOL", "expectedResult?", true)
				),
				"BOOL"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				boolean result = check();
				boolean expectedResult = ArgumentsHelper.getBoolean(arguments.get(0));
				if(result == expectedResult) {
					emitSignal("ONRUNTIMESUCCESS");
				}
				else {
					emitSignal("ONRUNTIMEFAILED");
				}
				return new BoolVariable("", result, context);
			}
		});
		this.setMethod("ONE_BREAK", new Method(
				List.of(
						new Parameter("BOOL", "expectedResult", true)
				),
				"void"
		) {
			@Override
			public Variable execute(List<Object> arguments) {
				boolean result = check();
				boolean expectedResult = ArgumentsHelper.getBoolean(arguments.get(0));
				if(result == expectedResult) {
					throw new OneBreakException("OneBreak statement encountered");
				}
				return null;
			}
		});
	}

	protected boolean check() {
		behaviourVariable.getMethod("RUN", Collections.singletonList("mixed"))
				.execute(null);
		Object checkResult = behaviourVariable.getContext().getReturnValue();
		return ArgumentsHelper.getBoolean(checkResult);
	}

	@Override
	public String getType() {
		return "CONDITION";
	}

	private String getOperator() {
		String operator = getAttribute("OPERATOR").getValue().toString();

		switch (operator) {
			case "EQUAL":
				return "_";
			case "NOTEQUAL":
				return "!_";
			case "LESS":
				return "<";
			case "GREATER":
				return ">";
			case "LESSEQUAL":
				return "<_";
			case "GREATEREQUAL":
				return ">_";
			default:
				return "_"; // TODO: throw exception
		}
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("OPERAND1", "OPERAND2", "OPERATOR");
		List<String> knownAttributesComplex = List.of("CONDITION1", "CONDITION2");
		if(knownAttributes.contains(name) || knownAttributesComplex.contains(name)) {
			super.setAttribute(name, attribute);

			if(getAttribute("OPERAND1") != null && getAttribute("OPERAND2") != null && getAttribute("OPERATOR") != null) {
				String code = "{@IF(\"" + getAttribute("OPERAND1").getValue().toString() + "\",\"" + getOperator() + "\",\"" + getAttribute("OPERAND2").getValue().toString() + "\",\"{@RETURN(TAK);}\",\"{@RETURN(NIE);}\");}";

				Context tmpContext = new Context();
				tmpContext.setParentContext(getContext());

				tmpContext.setVariable("TAK", new BoolVariable("TAK", true, tmpContext));
				tmpContext.setVariable("NIE", new BoolVariable("NIE", false, tmpContext));

				behaviourVariable = new BehaviourVariable("CONDITION_CODE", code, tmpContext);

            }
		}
	}


}
