package pl.cba.genszu.amcodetranslator.interpreter.ast.expressions;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.arithmetic.ArithmeticSolver;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;
import pl.cba.genszu.amcodetranslator.interpreter.factories.VariableFactory;

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
        System.out.println("DEBUG: " + leftValue.getValue() + " " + operator + " " + rightValue.getValue() + " = " + result);
        return new ConstantExpression(result);
    }

    private Variable getVariableFromObject(Object value, Context context) {
        if (value instanceof MethodCallExpression) {
            return (Variable) ((MethodCallExpression) value).evaluate(context);
        }
        else if(value instanceof ArithmeticExpression) {
            return VariableFactory.createVariableWithAutoType("",  ((ConstantExpression) ((ArithmeticExpression) value).evaluate(context)).evaluate(null).toString());
        }
        else if(value instanceof ConstantExpression) {
            Object valueString = ((ConstantExpression) value).evaluate(null);

            if(context.getVariable(valueString.toString()) == null) {
                return VariableFactory.createVariableWithAutoType("", valueString);
            }

            return context.getVariable(valueString.toString());
        }

        return null;
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
