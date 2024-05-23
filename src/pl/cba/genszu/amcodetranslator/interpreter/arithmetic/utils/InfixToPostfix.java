package pl.cba.genszu.amcodetranslator.interpreter.arithmetic.utils;

import pl.cba.genszu.amcodetranslator.interpreter.ast.Expression;
import pl.cba.genszu.amcodetranslator.interpreter.ast.expressions.OperatorExpression;

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

    public static List<String> infixToPostfix(List<String> infix) {
        //implementation of Djikstra's Shunting-Yard algorythm
        HashMap<String, Integer> prec = new HashMap<>();
        //prec.put("^", 3);
        prec.put("*", 2);
        prec.put("@", 2); //tak małpa to dzielenie, taaa
        prec.put("%", 2); //modulo chyba tak samo jak wyżej
        prec.put("+", 1);
        prec.put("-", 1);
        prec.put("(", -1);

        Stack<String> operators = new Stack<>();
        List<String> postfix = new ArrayList<>();

        for(String operand : infix) {
            if(!(operand.matches("[+\\-*@%()]") || operand.equals("OR") || operand.equals("AND")))
                postfix.add(operand);
            else if(operand.equals("("))
                operators.push(operand); // push it on stack
            else if(operand.equals(")")) {
                while(!operators.isEmpty() && !operators.peek().equals("(")) {
                    postfix.add(operators.pop());
                }
                try {
                    operators.pop(); //remove left parenthesis
                }
                catch(EmptyStackException e) {
                    System.out.println("DEBUG: pusty stack, wyrażenie => "+infix);
                    throw e;
                }
            }
            else {
                while (!operators.isEmpty() && (prec.get(operators.peek()) >= prec.get(operand))) {
                    postfix.add(operators.pop());
                }
                operators.push(operand); //push operand on stack
            }
        }

        while (!operators.isEmpty()) {
            if(operators.peek().equals("("))
                throw new RuntimeException("Mismatched parenthesis detected. Correct your expression.");
            postfix.add(operators.pop());
        }

        return postfix;
    }

    public static List<String> ifInfixToPostfix(List<String> infix) {
        HashMap<String, Integer> prec = new HashMap<>();
        prec.put("&&", 2);
        prec.put("||", 1);

        Stack<String> operators = new Stack<>();
        List<String> postfix = new ArrayList<>();

        for(String operand : infix) {
            if(!(operand.equals("&&") || operand.equals("||")))
                postfix.add(operand);
            else {
                while (!operators.isEmpty() && (prec.get(operators.peek()) >= prec.get(operand))) {
                    postfix.add(operators.pop());
                }
                operators.push(operand);
            }
        }

        while (!operators.isEmpty()) {
            postfix.add(operators.pop());
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
