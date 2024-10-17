package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.exceptions.ClassMethodNotImplementedException;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Method;
import pl.genschu.bloomooemulator.interpreter.variable.Parameter;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class ComplexConditionVariable extends ConditionVariable {
	private ConditionVariable condition1;
	private ConditionVariable condition2;
	private String operator;

	public ComplexConditionVariable(String name, Context context) {
		super(name, context);
	}

	@Override
	public String getType() {
		return "COMPLEXCONDITION";
	}

	@Override
	public void setAttribute(String name, Attribute attribute) {
		List<String> knownAttributes = List.of("CONDITION1", "CONDITION2", "OPERATOR");
		if(knownAttributes.contains(name)) {
			super.setAttribute(name, attribute);
            switch (name) {
                case "CONDITION1": {
                    Variable tmp = context.getVariable(attribute.getValue().toString());
                    if (tmp instanceof ConditionVariable) {
                        condition1 = (ConditionVariable) tmp;
                    }
                    break;
                }
                case "CONDITION2": {
                    Variable tmp = context.getVariable(attribute.getValue().toString());
                    if (tmp instanceof ConditionVariable) {
                        condition2 = (ConditionVariable) tmp;
                    }
                    break;
                }
                case "OPERATOR":
                    operator = (String) attribute.getValue();
                    break;
            }
		}
	}

	@Override
	protected boolean check() {
		if(condition1 == null) {
			Variable tmp = context.getVariable(getAttribute("CONDITION1").getValue().toString());
			if (tmp instanceof ConditionVariable) {
				condition1 = (ConditionVariable) tmp;
			}
		}
		if(condition2 == null) {
			Variable tmp = context.getVariable(getAttribute("CONDITION2").getValue().toString());
			if (tmp instanceof ConditionVariable) {
				condition2 = (ConditionVariable) tmp;
			}
		}
		if(condition1 == null || condition2 == null) {
			return false;
		}
		boolean result1 = ((BoolVariable) condition1.fireMethod("CHECK", new BoolVariable("", true, context))).GET();
		boolean result2 = ((BoolVariable) condition2.fireMethod("CHECK", new BoolVariable("", true, context))).GET();
		if(operator.equals("AND")) {
			return result1 && result2;
		} else if(operator.equals("OR")) {
			return result1 || result2;
		}
		return false;
	}
}
