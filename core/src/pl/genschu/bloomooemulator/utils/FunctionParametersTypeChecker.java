package pl.genschu.bloomooemulator.utils;

import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.interpreter.exceptions.InterpreterException;
import pl.genschu.bloomooemulator.interpreter.exceptions.InvalidMethodParamTypeException;

public class FunctionParametersTypeChecker {
    public static void checkTypes(Variable[] params, String[] types) {
        int requiredParams = 0;
        for(String type : types) {
            if(!type.startsWith("[") && !type.endsWith("]"))
                requiredParams++;
        }
        if(params.length < requiredParams)
            throw new InterpreterException(String.format("Oczekiwano %d wymaganych argumentów, a otrzymano %d.", requiredParams, params.length));
        //TODO: obsługa opcjonalnych parametrów w środku listy
        for (int i = 0; i < params.length; i++) {
            boolean required = !types[i].startsWith("[") && !types[i].endsWith("]");
            if (!required) {
                types[i] = types[i].substring(1, types[i].length()-1);
            }
            String paramType = params[i].getType();
            if (!paramType.equals(types[i])) {
                throw new InvalidMethodParamTypeException(types[i], paramType);
            }
        }
    }
}
