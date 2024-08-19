package pl.genschu.bloomooemulator.interpreter.ast.expressions;

import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.arithmetic.ArithmeticSolver;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;

import static pl.genschu.bloomooemulator.interpreter.util.VariableHelper.getVariableFromObject;

public class ArithmeticExpression extends Expression {
    private final Expression left;
    private final Expression right;
    private final String operator;

    public ArithmeticExpression(Expression left, Expression right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public Object evaluate(Context context) {
        Variable leftValue = getVariableFromObject(left, context);
        Variable rightValue = getVariableFromObject(right, context);
        Object result = performOperation(leftValue, rightValue, operator).getValue();

        assert leftValue != null;
        assert rightValue != null;
        // System.out.println("DEBUG: " + leftValue.getValue() + " " + operator + " " + rightValue.getValue() + " = " + result);
        return new ConstantExpression(result);
    }

    private Variable performOperation(Variable operand1, Variable operand2, String token) {
        switch (token) {
            case "+":
                return ArithmeticSolver.add(operand1, operand2);
            case "-":
                return ArithmeticSolver.subtract(operand1, operand2);
            case "*":
                return ArithmeticSolver.multiply(operand1, operand2);
            case "@": // division
                return ArithmeticSolver.divide(operand1, operand2);
            case "%":
                return ArithmeticSolver.modulo(operand1, operand2);
            default:
                throw new IllegalArgumentException("Invalid operator: " + token);
        }
    }
}
