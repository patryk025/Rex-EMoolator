package pl.genschu.bloomooemulator.interpreter.ast.expressions;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.logic.LogicSolver;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;

import static pl.genschu.bloomooemulator.interpreter.util.VariableHelper.getVariableFromObject;

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
        Variable leftValue = getVariableFromObject(left, context, true);
        Variable rightValue = getVariableFromObject(right, context, true);
        Object result = performOperation(leftValue, rightValue, operator).getValue();

        assert leftValue != null;
        assert rightValue != null;
        Gdx.app.log("ConditionExpression", "DEBUG: " + leftValue.getValue() + " " + operator + " " + rightValue.getValue() + " = " + result);
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
