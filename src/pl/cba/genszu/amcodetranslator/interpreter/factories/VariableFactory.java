package pl.cba.genszu.amcodetranslator.interpreter.factories;

import pl.cba.genszu.amcodetranslator.interpreter.*;

public class VariableFactory
{
    public static Variable<?> createVariable(String type, String name, Object value) {
        switch (type.toUpperCase()) {
            
            default:
                throw new IllegalArgumentException("Unknown variable type: " + type);
        }
    }
}
