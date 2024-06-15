package pl.cba.genszu.amcodetranslator.interpreter.ast.expressions;

import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.interpreter.ast.*;
import pl.cba.genszu.amcodetranslator.interpreter.logic.*;
import pl.cba.genszu.amcodetranslator.interpreter.variable.Variable;

import static pl.cba.genszu.amcodetranslator.interpreter.util.VariableHelper.getVariableFromObject;

public class ConditionExpression extends Expression {

    private Expression left;
    private Expression right;
    private String operator;

    public ConditionExpression(Expression left, Expression right, String operator) {
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

    private Variable performOperation(Variable operand1, Variable operand2, String token) {
        switch (token) {
            case "&&":
                return LogicSolver.and(operand1, operand2);
            case "||":
                return LogicSolver.or(operand1, operand2);
            case "'":
            case "_":
                return LogicSolver.equals(operand1, operand2);
            case "!'":
            case "!_":
                return LogicSolver.notEquals(operand1, operand2);
            case ">":
                return LogicSolver.greater(operand1, operand2);
            case ">'":
            case ">_":
                return LogicSolver.greaterOrEquals(operand1, operand2);
            case "<":
                return LogicSolver.less(operand1, operand2);
            case "<'":
            case "<_":
                return LogicSolver.lessOrEquals(operand1, operand2);
            default:
                throw new IllegalArgumentException("Invalid operator: " + token);
        }
    }
}
