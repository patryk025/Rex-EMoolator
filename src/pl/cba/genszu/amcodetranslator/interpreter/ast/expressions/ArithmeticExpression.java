package pl.cba.genszu.amcodetranslator.interpreter.ast.expressions;

import pl.cba.genszu.amcodetranslator.interpreter.Context;
import pl.cba.genszu.amcodetranslator.interpreter.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.arithmetic.ArithmeticSolver;
import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;

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
        Variable leftValue = (Variable) left.evaluate(context);
        Variable rightValue = (Variable) right.evaluate(context);
        return new ConstantExpression(performOperation(leftValue, rightValue, operator).getValue());
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
