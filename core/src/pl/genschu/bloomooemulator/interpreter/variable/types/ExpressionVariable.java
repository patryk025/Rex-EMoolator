package pl.genschu.bloomooemulator.interpreter.variable.types;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.arithmetic.ArithmeticSolver;
import pl.genschu.bloomooemulator.interpreter.variable.Attribute;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import java.util.Collections;
import java.util.List;

public class ExpressionVariable  extends Variable {
    // It's just fancy way to replace [operand1^operator(operand2)]
    private String operand1;
    private String operand2;
    private String operator;

    public ExpressionVariable(String name, Context context) {
        super(name, context);
    }

    public void setAttribute(String name, Attribute attribute) {
        List<String> knownAttributes = List.of("OPERAND1", "OPERAND2", "OPERATOR");
        if(knownAttributes.contains(name)) {
            super.setAttribute(name, attribute);

            if(getAttribute("OPERAND1") != null && getAttribute("OPERAND2") != null && getAttribute("OPERATOR") != null) {
                operand1 = getAttribute("OPERAND1").getValue().toString();
                operand2 = getAttribute("OPERAND2").getValue().toString();
                operator = getAttribute("OPERATOR").getValue().toString();
            }
        }
    }

    private Variable parseOperand(String operand) {
        if(operand.startsWith("\"") && operand.endsWith("\"")) {
            return new StringVariable("", operand.substring(1, operand.length() - 1), context);
        }

        if(operand.contains("^")) {
            // oh man, it's a function, let's create temporary behaviour
            BehaviourVariable behaviourVariable = new BehaviourVariable("", "{@RETURN("+operand+");}", context);

            return behaviourVariable.fireMethod("RUN");
        }

        return context.getVariable(operand);
    }

    @Override
    public String getType() {
        return "EXPRESSION";
    }

    @Override
    public Object getValue() {
        Variable operand1 = getValue(parseOperand(this.operand1));
        Variable operand2 = getValue(parseOperand(this.operand2));
        switch (operator) {
            case "ADD":
                return ArithmeticSolver.add(operand1, operand2);
            case "SUB":
                return ArithmeticSolver.subtract(operand1, operand2);
            case "MUL":
                return ArithmeticSolver.multiply(operand1, operand2);
            case "DIV":
                return ArithmeticSolver.divide(operand1, operand2);
            case "MOD":
                return ArithmeticSolver.modulo(operand1, operand2);
            default:
                Gdx.app.error("ExpressionVariable", "Unknown operator: " + operator);
                return null;
        }
    }

    private Variable getValue(Variable var) {
        if(var instanceof ExpressionVariable) {
            return (Variable) var.getValue();
        } else {
            return var;
        }
    }
}
