package pl.genschu.bloomooemulator.interpreter.arithmetic.utils;

import pl.genschu.bloomooemulator.interpreter.ast.Expression;
import pl.genschu.bloomooemulator.interpreter.ast.expressions.OperatorExpression;

import java.util.*;

public class InfixToPostfix {
    public static Deque<Expression> convertToPostfix(List<Expression> infix) {
        //implementation of Dijkstra's Shunting-Yard algorithm

        Stack<Expression> operatorStack = new Stack<>();
        Deque<Expression> postfix = new ArrayDeque<>();

        for(Expression operand : infix) {
            if(!(operand instanceof OperatorExpression)) {
                postfix.add(operand);
            }
            else {
                String operandVal = operand.evaluate(null).toString();

                if(operandVal.equals("(")) {
                    operatorStack.push(operand);
                }
                else if(operandVal.equals(")")) {
                    while(!operatorStack.peek().evaluate(null).toString().equals("(")) {
                        postfix.add(operatorStack.pop());
                    }
                    try {
                        operatorStack.pop();
                    }
                    catch (EmptyStackException e) {
                        throw new EmptyStackException();
                    }
                }
                else {
                    while(!operatorStack.isEmpty() && precedence(operandVal) <= precedence(operatorStack.peek().evaluate(null).toString())) {
                        postfix.add(operatorStack.pop());
                    }
                    operatorStack.push(operand);
                }
            }
        }

        while(!operatorStack.isEmpty()) {
            String operandVal = operatorStack.peek().evaluate(null).toString();
            if(operandVal.equals("(")) {
                throw new RuntimeException("Mismatched parenthesis detected. Correct your expression.");
            }
            postfix.add(operatorStack.pop());
        }

        return postfix;
    }

    private static int precedence(String operator) {
        switch (operator) {
            case "*":
            case "@":
            case "%":
            case "&&": // and
                return 2;
            case "+":
            case "-":
            case "||": // or
                return 1;
            default:
                return -1;
        }
    }
}
