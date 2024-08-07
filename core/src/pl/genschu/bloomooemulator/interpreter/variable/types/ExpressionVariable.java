package pl.genschu.bloomooemulator.interpreter.variable.types;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.List;

public class ExpressionVariable  extends Variable {
    // It's just fancy way to replace [operand1^operator(operand2)]
    private Variable operand1;
    private Variable operand2;
    private String operator;

    public ExpressionVariable(String name, Context context) {
        super(name, context);
    }

    public void setAttribute(String name, Attribute attribute) {
        List<String> knownAttributes = List.of("OPERAND1", "OPERAND2", "OPERATOR");
        if(knownAttributes.contains(name)) {
            super.setAttribute(name, attribute);

            if(getAttribute("OPERAND1") != null && getAttribute("OPERAND2") != null && getAttribute("OPERATOR") != null) {
                operand1 = parseOperand(getAttribute("OPERAND1").getValue().toString());
                operand2 = parseOperand(getAttribute("OPERAND2").getValue().toString());
                operator = getAttribute("OPERATOR").getValue().toString();
            }
        }
    }

    private Variable parseOperand(String operand) {
        if(operand.startsWith("\"") && operand.endsWith("\"")) {
            return new StringVariable("", operand.substring(1, operand.length() - 1), context);
        }

        return context.getVariable(operand);
    }

    @Override
    public String getType() {
        return "EXPRESSION";
    }

    @Override
    public Object getValue() {
        return operand1.fireMethod(operator, operand2);
    }
}
