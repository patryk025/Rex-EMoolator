package pl.cba.genszu.amcodetranslator.interpreter.util;

import pl.cba.genszu.amcodetranslator.interpreter.Variable;
import pl.cba.genszu.amcodetranslator.interpreter.exceptions.InterpreterException;
import pl.cba.genszu.amcodetranslator.interpreter.factories.VariableFactory;
import pl.cba.genszu.amcodetranslator.utils.InfixToPostfix;
import pl.cba.genszu.amcodetranslator.utils.TypeGuesser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ConditionChecker
{
    public static boolean check(List<String> ifElements) {
        Variable var1 = VariableFactory.createVariable(TypeGuesser.guessNumber(ifElements.get(0)), null, ifElements.get(0));
        Variable var2 = VariableFactory.createVariable(TypeGuesser.guessNumber(ifElements.get(2)), null, ifElements.get(2));
        switch(ifElements.get(1)) {
            case "<'":
            case "<_":
                if(var1.getType().equals("STRING") || var2.getType().equals("STRING"))
                    throw new InterpreterException("Operator mniejsze lub równe jest nieobsługiwany dla zmiennych typu String");
                else
                    return Double.parseDouble("" + var1.getValue()) <= Double.parseDouble("" + var2.getValue()); //czy int, czy double nie powinno mieć wpływu
            case "<":
                if(var1.getType().equals("STRING") || var2.getType().equals("STRING"))
                    throw new InterpreterException("Operator mniejsze niż jest nieobsługiwany dla zmiennych typu String");
                else
                    return Double.parseDouble("" + var1.getValue()) < Double.parseDouble("" + var2.getValue());
            case "'":
            case "_":
                if(var1.getType().equals("STRING") && !var2.getType().equals("STRING") || !var1.getType().equals("STRING") && var2.getType().equals("STRING"))
                    return false; //nie jest równe i tyle
                else if(var1.getType().equals("STRING") && var2.getType().equals("STRING"))
                    return var1.getValue().equals(var2.getValue());
                else
                    return Double.parseDouble("" + var1.getValue()) == Double.parseDouble("" + var2.getValue());
            case "!'":
            case "!_":
                if(var1.getType().equals("STRING") && !var2.getType().equals("STRING") || !var1.getType().equals("STRING") && var2.getType().equals("STRING"))
                    return true; //nie jest równe i tyle
                else if(var1.getType().equals("STRING") && var2.getType().equals("STRING"))
                    return !var1.getValue().equals(var2.getValue());
                else
                    return Double.parseDouble("" + var1.getValue()) != Double.parseDouble("" + var2.getValue());
            case ">":
                if(var1.getType().equals("STRING") || var2.getType().equals("STRING"))
                    throw new InterpreterException("Operator większe niż jest nieobsługiwany dla zmiennych typu String");
                else
                    return Double.parseDouble("" + var1.getValue()) > Double.parseDouble("" + var2.getValue());
            case ">'":
            case ">_":
                if(var1.getType().equals("STRING") || var2.getType().equals("STRING"))
                    throw new InterpreterException("Operator większe lub równe jest nieobsługiwany dla zmiennych typu String");
                else
                    return Double.parseDouble("" + var1.getValue()) >= Double.parseDouble("" + var2.getValue()); //czy int, czy double nie powinno mieć wpływu
            default:
                return false;
        }
    }

	public static boolean checkCondition(List<String> ifElements) {
        List<String> postfix = InfixToPostfix.ifInfixToPostfix(ifElements);
        if(ifElements.size() == 3) {
            return check(ifElements);
        }
        else if(ifElements.size() > 3) {
            Stack<Boolean> stack = new Stack<>();
            List<String> tmpArray = new ArrayList<>();
            for (String token : postfix) {
                if (token.equals("&&") || token.equals("||")) {
                    if (tmpArray.size() == 3) {
                        stack.push(check(tmpArray));
                        tmpArray.clear();
                    }
                    // Pop the top two operands from the stack.
                    Boolean operand2 = stack.pop();
                    Boolean operand1 = stack.pop();

                    // Perform the operation.
                    //Variable tmp_result = performOperation(operand1, operand2, token);
                    if (token.equals("&&"))
                        stack.push(operand2 && operand1);
                    else
                        stack.push(operand2 || operand1);
                } else {
                    if (tmpArray.size() < 3) {
                        tmpArray.add(token);
                    } else if (tmpArray.size() == 3) {
                        stack.push(check(tmpArray));
                        tmpArray.clear();
                        tmpArray.add(token);
                    }
                }
            }
            return stack.pop();
        }
        else {
            return false;
        }
    }
}
