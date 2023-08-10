package pl.cba.genszu.amcodetranslator.utils;

import java.util.*;

public class InfixToPostfix {
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
}
